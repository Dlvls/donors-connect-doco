package com.example.bloodbank006_3.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodbank006_3.MainActivity
import com.example.bloodbank006_3.home.HomeFragment
import com.example.bloodbank006_3.SharedPreferencesManager
import com.example.bloodbank006_3.auth.register.RegisterActivity
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var dao: UserDao
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(this)

        if (isLoggedIn()) {
            navigateToMain()
            return
        }

        initializeViewModel()

        setupClickListeners()
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferencesManager.isLoggedIn()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initializeViewModel() {
        val clientDatabase = ClientDatabase.getInstance(applicationContext)
        val appDatabase = clientDatabase?.appDatabase

        if (appDatabase != null) {
            dao = appDatabase.authDao()
            viewModel = LoginViewModel(dao, applicationContext)
        }
    }

    private fun setupClickListeners() {
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            getUserData()
        }
    }

    private fun getUserData() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModel.getLoginData(email, password) {
                navigateToMain()
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}