package com.example.bloodbank006_3.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.bloodbank006_3.data.ClientDatabase.Companion.getInstance
import com.example.bloodbank006_3.data.dao.FormDao
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.data.entity.UserEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class FormViewModel(app: Application) : AndroidViewModel(app) {

    private var formDao: FormDao? = null
    private lateinit var userDao: UserDao

    fun insertData(
        email: String,
        name: String,
        address: String,
        phoneNumber: String,
        age: String,
        bloodType: String,
        sex: String,
        weight: String,
        imageUri: String,
        donateTime: String,
        formUploader: String,
    ) {
        Completable.fromAction {
            FormEntitiy(0, "", "", "", "", "", "", "", "", "", "", "", "").apply {
                this.email = email
                this.name = name
                this.address = address
                this.phoneNumber = phoneNumber
                this.age = age
                this.bloodType = bloodType
                this.sex = sex
                this.weight = weight
                this.donateTime = donateTime
                this.imageUri = imageUri
                this.formUploader = formUploader
                formDao?.insertData(this)
            }

        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun getUserByEmail(email: String): LiveData<UserEntity?> {
        return userDao.getUserByEmail(email)
    }

    init {
        formDao = getInstance(app)?.appDatabase?.formDao()
        userDao = getInstance(app)!!.appDatabase.authDao()
    }

}