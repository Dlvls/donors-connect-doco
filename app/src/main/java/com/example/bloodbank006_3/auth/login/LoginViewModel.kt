package com.example.bloodbank006_3.auth.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.bloodbank006_3.SharedPreferencesManager
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val dao: UserDao,
    private val context: Context,
) : ViewModel() {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    data class LoginResult(val user: UserEntity?, val loginSuccess: Boolean)

    private fun loginUser(email: String, password: String): LiveData<LoginResult> {
        val loginResult = MutableLiveData<LoginResult>()

        val userLiveData = dao.getUserByEmail(email)
        userLiveData.observeForever(object : Observer<UserEntity?> {
            override fun onChanged(user: UserEntity?) {
                if (user != null && user.password == password) {
                    viewModelScope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                dao.deleteUserById(user.id)
                                dao.addUser(user)
                            }
                            loginResult.value =
                                LoginResult(user, true) // User logged in successfully
                        } catch (e: Exception) {
                            Log.e("LoginViewModel", "Error during login: ${e.message}")
                            loginResult.value =
                                LoginResult(null, false) // Error occurred during login
                        }
                    }
                } else {
                    loginResult.value = LoginResult(null, false) // Invalid email or password
                }
                userLiveData.removeObserver(this) // Remove the observer after getting the result
            }
        })

        return loginResult
    }

    fun getLoginData(email: String, password: String, callback: () -> Unit) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        // Observe the login result from the loginUser function
        loginUser(email, password).observeForever { loginResult ->
            if (loginResult.loginSuccess) {
                // User logged in successfully
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                setLoggedIn(true) // Store the login status

                // Retrieve the username from the LoginResult object
                val username = loginResult.user?.username ?: ""

                // Save the user data, including the username
                saveUserData(email, password, username)

                callback()

            } else {
                // Invalid email or password
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(email: String, password: String, username: String?) {
        val user =
            UserEntity(email = email, password = password, imageUri = "", username = username ?: "")
        sharedPreferencesManager.saveUserToSharedPreferences(user)
    }

    private fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferencesManager.setLoggedIn(loggedIn)
    }

}