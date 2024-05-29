package com.example.bloodbank006_3.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "donor_request_table")
@Parcelize
data class DonorRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val requester: String,
    val requesterValidate: Boolean,
    val calendarDetails: CalendarDetails,
    val additionalMessage: String,
    val status: RequestStatus,
    val uploadedBy: String // Add the email field to track the user who uploaded the form
) : Parcelable

@Parcelize
data class CalendarDetails(
    val date: String,
    val time: String,
) : Parcelable

enum class RequestStatus {
    PENDING,

    //    PROCESSED,
    ACCEPTED,
    DECLINED
}
