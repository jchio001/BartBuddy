package com.app.jonathan.willimissbart.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class BartIntegerAdapter {

    @FromJson
    fun fromJson(value: String): Int {
        return try {
            Integer.parseInt(value)
        } catch (e: NumberFormatException) {
            0
        }
    }

    @ToJson
    fun toJson(value: Int): String {
        return value.toString()
    }
}
