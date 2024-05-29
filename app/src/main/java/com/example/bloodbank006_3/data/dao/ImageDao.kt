package com.example.bloodbank006_3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bloodbank006_3.data.entity.ImageEntity

@Dao
interface ImageDao {
    @Insert
    fun insert(imageEntity: ImageEntity): Long

    @Query("SELECT * FROM image_table")
    fun getAllImages(): List<ImageEntity>
}