package com.example.bloodbank006_3.detail

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.entity.CalendarDetails
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.RequestStatus
import com.example.bloodbank006_3.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(
    private val context: Context,
    private val clientDatabase: ClientDatabase
) : ViewModel() {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val registeredFormUploadedUserEmail: MutableLiveData<String> = MutableLiveData()
    private val selectedFormUploader: MutableLiveData<String> = MutableLiveData()

    private lateinit var userDao: UserDao

    fun init(userDao: UserDao) {
        this.userDao = userDao
    }

    init {
        observeRegisteredFormUploadedUser()
    }

    private fun observeRegisteredFormUploadedUser() {
        clientDatabase.appDatabase.formDao().getAll().observeForever { users ->
            val registeredFormUploadedUser = users.firstOrNull()
            val email = registeredFormUploadedUser?.formUploader ?: ""
            registeredFormUploadedUserEmail.value = email

            // Set the selectedFormUploader value to the formUploader of the selected eligible list
            selectedFormUploader.value = registeredFormUploadedUser?.formUploader ?: ""
        }
    }


//    private fun observeRegisteredFormUploadedUser() {
//        clientDatabase.appDatabase.authDao().getUsers().observeForever { users ->
//            val registeredFormUploadedUser = users.firstOrNull()
//            val email = registeredFormUploadedUser?.email ?: ""
//            registeredFormUploadedUserEmail.value = email
//        }
//    }

    fun performRequest(requester: String, meetingDetails: String, additionalMessage: String) {
        val uploadedByEmail = getRegisteredFormUploadedUserEmail() ?: ""
        val formUploader = getSelectedFormUploader() ?: ""

        val donorRequestEntity = DonorRequestEntity(
            requester = requester,
            requesterValidate = true,
            calendarDetails = CalendarDetails(date = meetingDetails, time = ""),
            additionalMessage = additionalMessage,
            uploadedBy = formUploader,
            status = RequestStatus.PENDING,
        )

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Insert the donor request into the database
                clientDatabase.appDatabase.donorRequestDao().insertDonorRequest(donorRequestEntity)

                // Log the stored data
                Log.d(
                    "DetailViewModel",
                    "Donor Request Stored: Requester: $requester, Meeting Details: $meetingDetails, Additional Message: $additionalMessage, Uploaded By: $uploadedByEmail"
                )
                Log.d(
                    "DetailViewModel",
                    "Send Request To Form That Uploaded By: $formUploader"
                )
            }
        }
    }

//    private fun getRegisteredFormUploadedUserEmail(): String? {
//        return registeredFormUploadedUserEmail.value
//    }

    private fun getRegisteredFormUploadedUserEmail(): String? {
        return registeredFormUploadedUserEmail.value
    }

    private fun getSelectedFormUploader(): String? {
        return sharedPreferences.getString("formUploader", null)
    }


//    private fun getCurrentLoggedInUserEmail(): String? {
//        val userLiveData = clientDatabase.appDatabase.formDao().getUserByEmail("")
//        val user = userLiveData.value
//
//        // Check if the user entity is available in the LiveData
//        if (user != null) {
//            return user.email
//        }
//
//        val observer = object : Observer<UserEntity?> {
//            override fun onChanged(observedUser: UserEntity?) {
//                if (observedUser != null) {
//                    val email = observedUser.email
//                    // Log the retrieved email or perform any other necessary operations
//                    Log.d("DetailViewModel", "Logged In User: $email")
//                }
//                // Remove the observer after getting the user entity
//                userLiveData.removeObserver(this)
//            }
//        }
//
//        // Start observing the LiveData with the created observer
//        userLiveData.observeForever(observer)
//
//        return null
//    }

    fun getUserByEmail(email: String): LiveData<UserEntity?> {
        return userDao.getUserByEmail(email)
    }


}