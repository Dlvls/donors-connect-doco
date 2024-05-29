package com.example.bloodbank006_3.home

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.ClientDatabase.Companion.getInstance
import com.example.bloodbank006_3.data.dao.FormDao
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.data.entity.UserEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private lateinit var userDao: UserDao

    fun init(userDao: UserDao) {
        this.userDao = userDao
    }

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)
    var formData: LiveData<List<FormEntitiy>>

    //    private lateinit var allUserProfiles: LiveData<List<UserEntity>>
    private var formDao: FormDao? = null

    private val _profileImageUri = MutableLiveData<String?>()

//    suspend fun fetchProfileImage(email: String): String? {
//        return withContext(Dispatchers.IO) {
//            val imageUri = userDao.getProfileImages(email)
//            Log.d("MainViewModel", "Fetched profile image URI: $imageUri")
//            imageUri
//        }
//    }

    fun getUserByEmail(email: String): LiveData<UserEntity?> {
        return userDao.getUserByEmail(email)
    }

    fun getUserFormByEmail(email: String): LiveData<FormEntitiy?> {
        return formDao!!.getUserByEmail(email)
    }

//    fun updateUserProfile(userForm: UserEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            formDao?.updateProfileImage(userForm.email, userForm.imageUri)
//            Log.d("HomeViewModel", "User profile updated. Image URI: ${userForm.imageUri}")
//        }
//    }

    fun getProfileImageUri(): LiveData<String?> {
        return _profileImageUri
    }

//    private lateinit var userDao: UserDao

//    fun init(userDao: UserDao) {
//        this.userDao = userDao
//        allUserProfiles = userDao.getAllUserProfiles()
//    }

    private fun getFormDao(): FormDao {
        return formDao ?: throw IllegalStateException("FormDao is not initialized")
    }

    fun deleteDataById(formEntity: FormEntitiy) {
        Completable.fromAction {
            formDao?.deleteSingleData(formEntity.uid)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun onFormUploaderClick(formUploader: String) {
        // Save the formUploader value in SharedPreferences
        sharedPreferences.edit().putString("formUploader", formUploader).apply()

        Log.d("MainViewModel", "Clicked Form Uploader: $formUploader")
    }

    fun getSelectedFormUploader(): String? {
        // Retrieve the formUploader value from SharedPreferences
        return sharedPreferences.getString("formUploader", null)
    }

//    fun getAllUserProfiles(): LiveData<List<UserEntity>> {
//        return allUserProfiles
//    }

    init {
        formDao = getInstance(application)?.appDatabase?.formDao()
        formData = formDao?.getAll()!!

        userDao = getInstance(application)?.appDatabase!!.authDao()

    }
}