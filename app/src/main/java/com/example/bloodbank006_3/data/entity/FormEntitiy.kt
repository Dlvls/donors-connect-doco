package com.example.bloodbank006_3.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "blood_bank_table")
@Parcelize
data class FormEntitiy(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String,

    @ColumnInfo(name = "age")
    var age: String,

    @ColumnInfo(name = "donate_time")
    var donateTime: String,

    @ColumnInfo(name = "blood_type")
    var bloodType: String,

    @ColumnInfo(name = "sex")
    var sex: String,

    @ColumnInfo(name = "weight")
    var weight: String,

    @ColumnInfo(name = "image_uri")
    var imageUri: String,

    @ColumnInfo(name = "form_uploader")
    var formUploader: String,
) : Parcelable