package com.example.bloodbank006_3.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.data.entity.UserEntity

@Dao
interface FormDao {
    /** Form Dao **/
    @Query("SELECT * FROM blood_bank_table")
    fun getAll(): LiveData<List<FormEntitiy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(formEntity: FormEntitiy)

    @Query("DELETE FROM blood_bank_table WHERE uid = :uid")
    fun deleteSingleData(uid: Int)

    /** Donor Request Dao **/
    @Query("SELECT * FROM donor_request_table WHERE uid = :uid")
    fun getDonorRequestById(uid: Int): LiveData<DonorRequestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDonorRequest(donorRequest: DonorRequestEntity)

    @Query("SELECT email FROM user_table WHERE loggedIn = 1 LIMIT 1")
    fun getLoggedInUserEmail(): LiveData<String?>

    // For update images
    @Query("UPDATE blood_bank_table SET image_uri = :imageUri WHERE email = :email")
    fun updateProfileImage(email: String, imageUri: String)

    @Query("SELECT * FROM blood_bank_table WHERE email = :email")
    fun getUserByEmail(email: String): LiveData<FormEntitiy?>
}
