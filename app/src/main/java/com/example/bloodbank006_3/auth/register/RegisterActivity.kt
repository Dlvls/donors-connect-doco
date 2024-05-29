package com.example.bloodbank006_3.auth.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bloodbank006_3.auth.login.LoginActivity
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var dao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        initializeDatabase()
        setupRegistrationButton()
    }

    private fun setupViews() {
        binding.tvSignUpLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun initializeDatabase() {
        val clientDatabase = ClientDatabase.getInstance(applicationContext)
        val appDatabase = clientDatabase?.appDatabase

        if (appDatabase != null) {
            dao = appDatabase.authDao()
            viewModel = RegisterViewModel(dao) // Initialize RegisterViewModel
        } else {
            val errorMessage = "Failed to initialize the database"
            Log.e("RegisterActivity", errorMessage)

            finish() // Finish the activity or take appropriate action
            return
        }
    }

    private fun setupRegistrationButton() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch {
                val registrationSuccess = viewModel.registerUser(email, username, password)
                if (registrationSuccess) {
                    showRegistrationSuccessfulToast()
                    navigateToLoginActivity()
                } else {
                    showRegistrationErrorToast()
                }
            }
        }
    }

    private fun showRegistrationSuccessfulToast() {
        Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showRegistrationErrorToast() {
        Toast.makeText(
            this@RegisterActivity,
            "User with the same email already exists or invalid password",
            Toast.LENGTH_SHORT
        ).show()
    }
}

