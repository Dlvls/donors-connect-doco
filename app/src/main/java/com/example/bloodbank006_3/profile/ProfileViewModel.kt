package com.example.bloodbank006_3.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloodbank006_3.SharedPreferencesManager
import com.example.bloodbank006_3.auth.login.LoginActivity
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private lateinit var userDao: UserDao
    private lateinit var allUserProfiles: LiveData<List<UserEntity>>
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    fun init(userDao: UserDao) {
        this.userDao = userDao
        allUserProfiles = userDao.getAllUserProfiles()
    }

    fun getAllUserProfiles(): LiveData<List<UserEntity>> {
        return allUserProfiles
    }

    fun getUserByEmail(email: String): LiveData<UserEntity?> {
        return userDao.getUserByEmail(email)
    }

    fun insertUserProfile(userProfile: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insertUserProfile(userProfile)
        }
    }

    fun updateUserProfile(userProfile: UserEntity) {
//        Log.d(TAG, "Updating user profile: $userProfile")
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateProfileImage(userProfile.email, userProfile.imageUri)
        }
    }

    private fun setLoggedIn(loggedIn: Boolean, context: Context) {
        val sharedPreferences = context.getSharedPreferences("login_status", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("logged_in", loggedIn)
        editor.apply()
    }
}