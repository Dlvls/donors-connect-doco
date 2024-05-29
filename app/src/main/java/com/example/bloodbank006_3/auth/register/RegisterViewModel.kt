package com.example.bloodbank006_3.auth.register

import androidx.lifecycle.ViewModel
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterViewModel(private val dao: UserDao) : ViewModel() {

    suspend fun registerUser(email: String, username: String, password: String): Boolean {
        // Perform password validation
        if (!isPasswordValid(password)) {
            return false
        }

        val newUser = UserEntity(
            email = email,
            username = username,
            password = password,
            imageUri = ""
        )

        withContext(Dispatchers.IO) {
            dao.addUser(newUser)
        }

        return true // User registered successfully
    }

    private fun isPasswordValid(password: String): Boolean {
        val pattern = "(?=.*[0-9])(?=.*[a-zA-Z]).{6,}".toRegex()
        return password.matches(pattern)
    }

}