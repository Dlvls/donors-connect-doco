package com.example.bloodbank006_3.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bloodbank006_3.SharedPreferencesManager
import com.example.bloodbank006_3.auth.login.LoginActivity
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.data.entity.UserEntity
import com.example.bloodbank006_3.databinding.FragmentHomeBinding
import com.example.bloodbank006_3.detail.DetailActivity
import com.example.bloodbank006_3.notification.NotificationListActivity
import com.example.bloodbank006_3.slider.SlideActivity

class HomeFragment : Fragment(), HomeAdapter.MainAdapterCallback {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var viewModel: HomeViewModel

    private lateinit var loggedInUser: UserEntity
    private var loggedInEmail: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        val sharedPreferences =
            requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")

        loggedInUser = getUserFromSharedPreferences()
        loggedInEmail = getUserFromSharedPreferences().email

        // Check if the user is logged in
        if (!sharedPreferencesManager.isLoggedIn()) {
            navigateToLogin()
            return view
        }

        // Retrieve email from SharedPreferences
//        val email = sharedPreferencesManager.getEmail()
        val username = sharedPreferencesManager.getUsername()

        // Set email in TextView
//        binding.tvEmail.text = email
        binding.tvMainUsername.text = username

//        binding.mainProfileImage.setOnClickListener {
//            logoutUser()
//        }

        binding.icNotification.setOnClickListener {
//            logoutUser()
            startActivity(Intent(requireContext(), NotificationListActivity::class.java))
        }

        binding.cardView.setOnClickListener {
            startActivity(Intent(requireContext(), SlideActivity::class.java))
        }

        setViewModel()
        setLayout()
        loadImage()
//        setUpBottomNavigation()

//        viewModel.updateUserProfile(loggedInUser)

        return view
    }

    private fun setLayout() {
        binding.apply {
            tvNotFound.visibility = View.GONE
            homeAdapter = HomeAdapter(ArrayList(), this@HomeFragment)
//            homeAdapter.setData(formEntities)
            rvHistory.setHasFixedSize(true)
            rvHistory.layoutManager = LinearLayoutManager(requireContext())
            rvHistory.adapter = homeAdapter
        }
    }

//    private fun setLayout() {
//        val imageUri = ""
//
//        binding.apply {
//            tvNotFound.visibility = View.GONE
//            homeAdapter = HomeAdapter(ArrayList(), this@HomeFragment, imageUri)
//            rvHistory.setHasFixedSize(true)
//            rvHistory.layoutManager = LinearLayoutManager(requireContext())
//            rvHistory.adapter = homeAdapter
//        }
//    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        viewModel.formData.observe(viewLifecycleOwner) { formEntities: List<FormEntitiy> ->
            if (formEntities.isEmpty()) {
                binding.tvNotFound.visibility = View.VISIBLE
                binding.mainImgNotFound.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvNotFound.visibility = View.GONE
                binding.mainImgNotFound.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                homeAdapter.setData(formEntities)
            }
        }
    }

    override fun onDelete(model: FormEntitiy) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setMessage("Delete this list?")
        alertDialogBuilder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            model.let { viewModel.deleteDataById(it) }
            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT)
                .show()
        }

        alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onItemClick(model: FormEntitiy) {
        // Implementation for onItemClick method
        model.let {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("donor", model)
            startActivity(intent)
        }
    }

    override fun onFormUploaderClick(formUploader: String) {
        viewModel.onFormUploaderClick(formUploader)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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

    private fun navigateToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadImage() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")

        viewModel.getUserByEmail(email.toString()).observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                // Retrieve the imageUri from the userProfile
                val imageUri = userProfile.imageUri

                // Save the imageUri in SharedPreferences
                sharedPreferences.edit().putString("imageUri", imageUri).apply()

                // Use the imageUri to load and display the image in your ImageView
                // For example, using Glide library:
                Glide.with(requireActivity())
                    .load(imageUri)
                    .into(binding.mainProfileImage)
            }
        }
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
}
