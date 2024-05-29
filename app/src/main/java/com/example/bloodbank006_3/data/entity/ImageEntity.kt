package com.example.bloodbank006_3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imagePath: String
)