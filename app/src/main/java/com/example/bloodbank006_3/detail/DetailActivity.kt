package com.example.bloodbank006_3.detail

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.bloodbank006_3.R
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.databinding.ActivityDetailBinding
import com.example.bloodbank006_3.MainActivity
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientDatabase = ClientDatabase.getInstance(applicationContext)!!
        viewModel = DetailViewModel(this, clientDatabase)

        // Initialize the userDao property in the viewModel
        viewModel.init(clientDatabase.appDatabase.authDao())

        binding.btnRequestDonor.setOnClickListener {
            val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
            val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
            val snackbarView = LayoutInflater.from(snackbarLayout.context)
                .inflate(R.layout.item_request, snackbarLayout, false)

            val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("email", "")

            // Set up calendar and additional message fields
            val datePicker: DatePicker = snackbarView.findViewById(R.id.datePicker)
            val etAdditionalMessage: EditText = snackbarView.findViewById(R.id.etAdditionalMessage)

            // Set up the "Request" button inside the snackbar
            val btnRequest: Button = snackbarView.findViewById(R.id.btnRequest)
            btnRequest.setOnClickListener {
                // Handle the request logic
                val year = datePicker.year
                val month = datePicker.month
                val dayOfMonth = datePicker.dayOfMonth

                val requester = email.toString()
                val meetingDetails = "$year-$month-$dayOfMonth"
                val additionalMessage = etAdditionalMessage.text.toString()

                // Perform the donor request using the ViewModel
                viewModel.performRequest(requester, meetingDetails, additionalMessage)

                // Display a success message
                val successMessage = getString(R.string.request_success)
                val successSnackbar =
                    Snackbar.make(binding.root, successMessage, Snackbar.LENGTH_LONG)
                successSnackbar.setActionTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.success_green
                    )
                )
                successSnackbar.show()

                // Dismiss the snackbar
                snackbar.dismiss()
            }

            // Add the custom view to the Snackbar layout
            snackbarLayout.addView(snackbarView, 0)

            // Set the background color of the Snackbar layout
            snackbarLayout.setBackgroundColor(Color.WHITE)

            // Set the text color of the DatePicker header
            val headerView = datePicker.findViewById<ViewGroup>(
                Resources.getSystem()
                    .getIdentifier("android:id/date_picker_header", null, null)
            )
            val headerMonthTextView = headerView.findViewById<TextView>(
                Resources.getSystem()
                    .getIdentifier("android:id/date_picker_header_date", null, null)
            )
            val headerYearTextView = headerView.findViewById<TextView>(
                Resources.getSystem()
                    .getIdentifier("android:id/date_picker_header_year", null, null)
            )
            headerMonthTextView.setTextColor(Color.WHITE)
            headerYearTextView.setTextColor(Color.WHITE)

            // Set up the back button click listener
            val btnBack: ImageButton = snackbarView.findViewById(R.id.btnBack)
            btnBack.setOnClickListener {
                // Dismiss the snackbar
                snackbar.dismiss()
            }

            // Show the snackbar
            snackbar.show()
        }

        binding.icBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Retrieve the FormEntity object from the intent extra
        val formEntity = intent.getParcelableExtra<FormEntitiy>("donor")

        // Display the name in the appropriate TextView
        binding.tvName.text = formEntity?.name

        // Retrieve the drawable resource ID
        val drawableResId = R.drawable.img_wa // Replace with your actual drawable resource ID

        // Load the drawable into Glide directly
        Glide.with(this)
            .load(drawableResId)
            .override(500, 500) // Set the desired width and height for the compressed image
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imgWa)

        // Display the address in the appropriate TextView
        binding.tvAddress.text = formEntity?.address

        // Display the phone number in the appropriate TextView
        binding.tvPhoneNumber.text = formEntity?.phoneNumber

        // Display the blood type in the appropriate TextView
        binding.tvBloodType.text = formEntity?.bloodType

        // Display the age in the appropriate TextView
        binding.tvAge.text = formEntity?.age.toString()

        // Display the sex in the appropriate TextView
        binding.tvSex.text = formEntity?.sex

        binding.tvWeight.text = formEntity?.weight

        Glide.with(this)
            .load(formEntity?.imageUri)
            .into(binding.roundedImageView2)

        // Set click listener on the ImageView to open WhatsApp for chatting
        binding.imgWa.setOnClickListener {
            val phoneNumber = binding.tvPhoneNumber.text.toString()

            // Check if WhatsApp is installed on the device
            val packageManager = packageManager
            val isWhatsAppInstalled: Boolean = try {
                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }

            if (isWhatsAppInstalled) {
                // Remove leading zero from the phone number
                val formattedPhoneNumber = phoneNumber.removePrefix("0")

                // Format the phone number with the country code
                val countryCode = "+62" // Replace with the appropriate country code
                val formattedPhoneNumberWithCountryCode = countryCode + formattedPhoneNumber

                // Open WhatsApp for chatting with the formatted phone number
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhoneNumberWithCountryCode")
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "WhatsApp is not installed on your device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
//        loadImage()
    }

//    private fun loadImage() {
//        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
//        val email = sharedPreferences.getString("email", "")
//
//        viewModel.getUserByEmail(email.toString()).observe(this) { userProfile ->
//            if (userProfile != null) {
//                // Display the image using the imageUri
//                val imageUri = userProfile.imageUri
//
////                Log.d(TAG, "User Profile Email: ${userProfile.email}")
////                Log.d(TAG, "LoggedIn Email: $loggedInEmail")
////                Log.d(TAG, "Image URI: $imageUri")
//
//                // Use the imageUri to load and display the image in your ImageView
//                // For example, using Glide library:
//                Glide.with(this)
//                    .load(imageUri)
//                    .into(binding.roundedImageView2)
//            } else {
////                Log.d(TAG, "User profile not found for email: $loggedInEmail")
//            }
//        }
//    }
}