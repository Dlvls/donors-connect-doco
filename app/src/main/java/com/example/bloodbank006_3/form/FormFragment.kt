package com.example.bloodbank006_3.form

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bloodbank006_3.MainActivity
import com.example.bloodbank006_3.R
import com.example.bloodbank006_3.databinding.FragmentFormBinding
import com.google.android.material.snackbar.Snackbar

class FormFragment : Fragment() {

    private lateinit var binding: FragmentFormBinding
    private lateinit var viewModel: FormViewModel
    private lateinit var strNama: String
    private lateinit var strPhoneNumber: String
    private lateinit var strAlamat: String
    private lateinit var strAge: String
    private lateinit var strWeight: String

    private var strEmail: String = ""
    private var strImageUri: String = ""

    private lateinit var strBloodType: Array<String>
    private lateinit var strSex: Array<String>
    private lateinit var strDonorTime: Array<String>

    private lateinit var strBloodTypeSelected: String
    private lateinit var strSexSelected: String
    private lateinit var strDonorTimeSelected: String

    interface ImageUriCallback {
        fun onImageUriReady(imageUri: String?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FormViewModel::class.java]

        setToolbar()
        setupData()
        setInputData()

//        binding.icBack.setOnClickListener {
//            startActivity(Intent(requireContext(), MainActivity::class.java))
//        }
    }

    private fun setToolbar() {
        requireActivity().actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupData() {
        strBloodType = resources.getStringArray(R.array.golongan_darah)
        strSex = resources.getStringArray(R.array.jenis_kelamin)
        strDonorTime = resources.getStringArray(R.array.jangka_waktu_donor)

        val arrayLang =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, strBloodType)
        arrayLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formSpinnerBloodType.adapter = arrayLang

        binding.formSpinnerBloodType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    strBloodTypeSelected = parent.getItemAtPosition(position).toString()
                    binding.formSpinnerBloodType.isEnabled = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val arrayLangSex =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, strSex)
        arrayLangSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formSpinnerSex.adapter = arrayLangSex

        binding.formSpinnerSex.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    strSexSelected = parent.getItemAtPosition(position).toString()
                    binding.formSpinnerSex.isEnabled = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val arrayLangDonorTime =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, strDonorTime)
        arrayLangDonorTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.formSpinnerDonorsTime.adapter = arrayLangDonorTime

        binding.formSpinnerDonorsTime.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    strDonorTimeSelected = parent.getItemAtPosition(position).toString()
                    binding.formSpinnerDonorsTime.isEnabled = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setInputData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")

        binding.btnForm.setOnClickListener {
            strEmail = email.toString()
            Log.d("FormFragment", "Form Email $strEmail")

            getImageUri(object : ImageUriCallback {
                override fun onImageUriReady(imageUri: String?) {
                    strImageUri = imageUri ?: ""
                    Log.d("FormFragment", "strImageUri $strImageUri")

                    strNama = binding.edtFormName.text.toString()
                    strAlamat = binding.edtFormAddress.text.toString()
                    strPhoneNumber = binding.edtFormPhone.text.toString()
                    strAge = binding.edtFormAge.text.toString()
                    strWeight = binding.edtFormWeight.text.toString()

                    if (validateInput()) {
                        viewModel.insertData(
                            strEmail,
                            strNama,
                            strAlamat,
                            strPhoneNumber,
                            strAge,
                            strBloodTypeSelected,
                            strSexSelected,
                            strWeight,
                            strImageUri,
                            strDonorTimeSelected,
                            email.toString()
                        )
                        Log.d("FormActivity", "Form Uploader Email: $strEmail")
                        Log.d("FormActivity", "Form Nama: $strNama")
                        Log.d("FormActivity", "Form Alamat: $strAlamat")
                        Log.d("FormActivity", "Form Phone Number: $strPhoneNumber")
                        Log.d("FormActivity", "Form Age: $strAge")
                        Log.d("FormActivity", "Form Weight: $strWeight")
                        Log.d("FormActivity", "Form Blood Type: $strBloodTypeSelected")
                        Log.d("FormActivity", "Form Sex: $strSexSelected")
                        Log.d("FormActivity", "Form Donor Time: $strDonorTimeSelected")
                        Log.d("FormActivity", "Form Image URI: $strImageUri")
                        Log.d("FormActivity", "Form User Email: ${email.toString()}")

                        Log.d("FormActivity", "Form Uploader Email: $email") // Log the email
                        Toast.makeText(requireContext(), "Uploaded!", Toast.LENGTH_SHORT).show()

                        // Start MainActivity or HomeActivity
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }
    }

    private fun validateInput(): Boolean {
        if (strNama.isEmpty() || strAlamat.isEmpty() || strPhoneNumber.isEmpty() || strAge.isEmpty()) {
            showSnackbar(requireContext(), "Please fill all the form!")
            return false
        } else if ((strAge.toInt() < 18) || (strAge.toInt() > 59)) {
            showSnackbar(
                requireContext(),
                "You're not eligible to donate! Click here to see the donor requirement"
            )
            return false
        } else if (strDonorTimeSelected == "Ya") {
            showSnackbar(
                requireContext(),
                "You're not eligible to donate! Click here to see the donor requirement"
            )
            return false
        }
        return true
    }

    private fun showSnackbar(context: Context, message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL // Set gravity to the top center
        params.topMargin = resources.getDimensionPixelSize(R.dimen.snackbar_margin_top) // Add top margin
        snackbarView.layoutParams = params

        snackbar.setAction("Dismiss") { snackbar.dismiss() }

        snackbar.show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getImageUri(callback: ImageUriCallback) {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")

        Log.d("FormFragment", "The Email: $email")

        viewModel.getUserByEmail(email.toString()).observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                val imageUri = userProfile.imageUri
                Log.d("FormFragment", "Image URI: $imageUri")
                callback.onImageUriReady(imageUri)
            }
        }
    }

}
