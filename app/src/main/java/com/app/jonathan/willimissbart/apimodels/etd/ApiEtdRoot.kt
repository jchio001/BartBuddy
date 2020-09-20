package com.app.jonathan.willimissbart.apimodels.etd

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class ApiEtdRoot(
    @Json(name = "date") val date: String,
    @Json(name = "time") val time: String,
    // For some reason, this is always a list of 1.
    @Json(name = "station") val apiEtdStations: List<ApiEtdStation>
) {

    val queryTimeInEpoch: Long
        get() = DATE_FORMATTER.parse("$date $time").time

    companion object {

        private val DATE_FORMATTER = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa z", Locale.US)
    }
}
