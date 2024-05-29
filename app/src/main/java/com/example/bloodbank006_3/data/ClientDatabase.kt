package com.example.bloodbank006_3.data

import android.content.Context
import androidx.room.Room

class ClientDatabase private constructor(context: Context) {

    var appDatabase: AppDatabase

    companion object {
        private var mInstance: ClientDatabase? = null

        fun getInstance(context: Context): ClientDatabase? {
            if (mInstance == null) {
                mInstance = ClientDatabase(context)
            }
            return mInstance
        }
    }

    init {
        appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "bloodbank_db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
