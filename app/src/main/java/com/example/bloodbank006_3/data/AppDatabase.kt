package com.example.bloodbank006_3.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bloodbank006_3.CalendarDetailsConverter
import com.example.bloodbank006_3.data.dao.DonorRequestDao
import com.example.bloodbank006_3.data.dao.FormDao
import com.example.bloodbank006_3.data.dao.ImageDao
import com.example.bloodbank006_3.data.dao.UserDao
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.data.entity.ImageEntity
import com.example.bloodbank006_3.data.entity.UserEntity

@Database(
    entities = [FormEntitiy::class, DonorRequestEntity::class, UserEntity::class, ImageEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(CalendarDetailsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): UserDao
    abstract fun formDao(): FormDao
    abstract fun imageDao(): ImageDao
    abstract fun donorRequestDao(): DonorRequestDao
}