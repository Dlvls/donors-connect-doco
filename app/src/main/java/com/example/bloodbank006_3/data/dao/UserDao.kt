package com.example.bloodbank006_3.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bloodbank006_3.data.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getUsers(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun getUserByEmail(email: String): LiveData<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: UserEntity)

//    @Query("SELECT email FROM user_table WHERE loggedIn = 1 LIMIT 1")
//    fun getLoggedInUserEmail(): LiveData<String?>

//    @Query("SELECT * FROM user_table WHERE loggedIn = 1 LIMIT 1")
//    fun getLoggedInUser(): UserEntity?

    @Query("DELETE FROM user_table WHERE id = :id")
    fun deleteUserById(id: Int)

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

//    @Query("SELECT username FROM user_table WHERE id = :userId")
//    fun getUsername(userId: Int): LiveData<String>

    @Query("SELECT * FROM user_table")
    fun getAllUserProfiles(): LiveData<List<UserEntity>>

    @Insert
    fun insertUserProfile(userEntity: UserEntity)

    // For update images
    @Query("UPDATE user_table SET image_uri = :imageUri WHERE email = :email")
    fun updateProfileImage(email: String, imageUri: String)

    @Query("SELECT image_uri FROM user_table WHERE email = :email")
    fun getProfileImages(email: String): String

//    @Update
//    fun updateUserProfile(userEntity: UserEntity)

//    @Delete
//    fun deleteUserProfile(userEntity: UserEntity)
}