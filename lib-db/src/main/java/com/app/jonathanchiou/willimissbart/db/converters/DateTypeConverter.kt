package com.app.jonathanchiou.willimissbart.db.converters

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {

    @TypeConverter fun toDate(dateLong: Long): Date {
        return Date(dateLong)
    }

    @TypeConverter fun fromDate(date: Date): Long {
        return date.time
    }
}
