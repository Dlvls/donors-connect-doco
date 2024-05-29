package com.example.bloodbank006_3.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.RequestStatus

@Dao
interface DonorRequestDao {
    @Query("SELECT * FROM donor_request_table")
    fun getAllDonorRequests(): LiveData<List<DonorRequestEntity>>

    @Query("SELECT * FROM donor_request_table WHERE uid = :uid")
    fun getDonorRequestById(uid: Int): LiveData<DonorRequestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDonorRequest(donorRequest: DonorRequestEntity)

    // For update status
//    @Query("UPDATE donor_request_table SET status = :status WHERE uploadedBy= :email")
//    fun updateDonateStatus(email: String, status: RequestStatus)

    // For update status
    @Query("UPDATE donor_request_table SET status = :status WHERE uploadedBy= :email")
    fun updateRequestStatus(email: String, status: RequestStatus)

    @Query("UPDATE donor_request_table SET status = :status, requesterValidate = :requesterValidateFalse WHERE uploadedBy= :email AND requesterValidate = :requesterValidateTrue")
    fun updateRequestStatusValidate(
        email: String,
        requesterValidateTrue: Boolean,
        status: RequestStatus,
        requesterValidateFalse: Boolean
    )

    @Query("SELECT status FROM donor_request_table WHERE uploadedBy = :email")
    fun getRequestStatus(email: String): LiveData<RequestStatus>

    // Add other necessary queries or operations
}