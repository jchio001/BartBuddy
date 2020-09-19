package com.app.jonathan.willimissbart.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {

    private val formatter = SimpleDateFormat("EEE MMM dd yyyy hh:mm a zzz", Locale.US)

    @FromJson
    fun fromJson(value: String): Date {
        return formatter.parse(value)!!
    }

    @ToJson
    fun toJson(value: Date): String {
        return value.toString()
    }
}
