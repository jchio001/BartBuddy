package com.app.jonathanchiou.willimissbart.trips.models.api

import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class Leg(
    @Json(name = "@origin") val origin: String,
    @Json(name = "@destination") val destination: String,
    @Json(name = "@trainHeadStation") val trainHeadStation: String,
    @Json(name = "@origTimeDate") val originDate: String,
    @Json(name = "@origTimeMin") val originDateMinutes: String,
    @Json(name = "@destTimeDate") val destinationDate: String,
    @Json(name = "@destTimeMin") val destinationDateMinutes: String
) {

    val duration: Int
        get() {
            val originTimeMinutes = (DATE_FORMATTER.parse(
                "${originDate} ${originDateMinutes}"
            ).time / 60000) * 60000
            val destinationTimeMinutes = (DATE_FORMATTER.parse(
                "${destinationDate} ${destinationDateMinutes}"
            ).time / 60000) * 60000

            return ((destinationTimeMinutes - originTimeMinutes) / 60000).toInt()
        }

    companion object {

        private val DATE_FORMATTER = SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.US)
    }
}
