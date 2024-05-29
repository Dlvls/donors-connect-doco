package com.example.bloodbank006_3.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.bloodbank006_3.ImageUtils
import com.example.bloodbank006_3.SharedPreferencesManager
import com.example.bloodbank006_3.auth.login.LoginActivity
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.entity.UserEntity
import com.example.bloodbank006_3.databinding.FragmentProfileBinding
import com.example.bloodbank006_3.profile.pager.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.ByteArrayOutputStream
import java.io.File

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private val tabTitles = arrayOf("Donations", "Requests")
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val pickImageRequest = 1
    private lateinit var loggedInUser: UserEntity
    private var loggedInEmail: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDao = ClientDatabase.getInstance(requireContext())?.appDatabase?.authDao()
        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        viewModel.init(userDao!!)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        loggedInUser = getUserFromSharedPreferences()

        loggedInEmail = getUserFromSharedPreferences().email

        binding.btnAdd.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

//        binding.icBack.setOnClickListener {
//            requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
//        }

        binding.tvUsername.text = loggedInUser.username

        binding.tvEmail.text = loggedInUser.email

        loadImage()

        requestStoragePermission()
        setPager()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImageRequest)
    }

    private fun loadImage() {
        viewModel.getUserByEmail(loggedInEmail).observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                val imageUri = userProfile.imageUri
                Log.d(TAG, "Loading image from URI: $imageUri")
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(binding.imgSetting)
            } else {
                Log.d(TAG, "User profile not found")
            }
        }
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission already granted
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data

            val contentResolver = requireContext().contentResolver

            if (imageUri != null) {
                val bitmap = ImageUtils.uriToBitmap(contentResolver, imageUri)

                if (bitmap != null) {
                    // Log the original bitmap dimensions
                    Log.d(TAG, "Original bitmap dimensions: ${bitmap.width} x ${bitmap.height}")

                    val username = loggedInUser.username
                    val timestamp = System.currentTimeMillis()
                    val tempFileName = "temp_image_$username$timestamp.png"
                    val tempFile = File(requireContext().cacheDir, tempFileName)

                    if (loggedInUser.imageUri.isNotEmpty()) {
                        val oldImageFile = File(loggedInUser.imageUri)
                        val oldImageFileName = oldImageFile.name
                        val isDeleted = oldImageFile.delete()
                        Log.d(TAG, "Old image file deleted: $isDeleted")
                    } else {
                        Log.d(TAG, "No old image file found.")
                    }

                    ImageUtils.saveBitmapToFile(bitmap, tempFile)

                    loggedInUser.imageUri = tempFile.absolutePath

                    viewModel.updateUserProfile(loggedInUser)

                    loadImage()

                    val storedBitmap = bitmapToString(bitmap)
                    Log.d(TAG, "Bitmap stored in the database: $storedBitmap")

                    // Log whether the image is stored in the database or not
                    if (loggedInUser.imageUri.isNotEmpty()) {
                        Log.d(TAG, "Image stored in the database")
                    } else {
                        Log.d(TAG, "Failed to store image in the database")
                    }

                    // Log the stored bitmap dimensions
                    val storedBitmapDimensions =
                        ImageUtils.getBitmapDimensions(tempFile.absolutePath)
                    Log.d(TAG, "Stored bitmap dimensions: $storedBitmapDimensions")
                } else {
                    // Failed to convert URI to bitmap
                    // Handle the error case
                    Log.e(TAG, "Failed to convert URI to bitmap")
                }
            } else {
                // Image URI is null
                // Handle the error case
                Log.e(TAG, "Image URI is null")
            }
        }
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getUserFromSharedPreferences(): UserEntity {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        val email = sharedPreferences.getString("email", "") ?: ""
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        val loggedIn = sharedPreferences.getBoolean("logged_in", false)
        val imageUri = sharedPreferences.getString("image_uri", "") ?: ""

        return UserEntity(userId, email, username, password, loggedIn, imageUri)
    }

    private fun setPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val pagerAdapter = PagerAdapter(requireActivity(), tabTitles)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun logoutUser() {
        // Clear the login status
        sharedPreferencesManager.setLoggedIn(false)

        // Navigate back to the LoginActivity
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        // Finish the MainActivity to prevent going back to it on back press
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 100
    }
}