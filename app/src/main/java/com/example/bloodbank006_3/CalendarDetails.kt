package com.example.bloodbank006_3

import androidx.room.TypeConverter
import com.example.bloodbank006_3.data.entity.CalendarDetails
import com.google.gson.Gson

class CalendarDetailsConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromCalendarDetails(calendarDetails: CalendarDetails): String {
        return gson.toJson(calendarDetails)
    }

    @TypeConverter
    fun toCalendarDetails(calendarDetailsString: String): CalendarDetails {
        return gson.fromJson(calendarDetailsString, CalendarDetails::class.java)
    }
}
