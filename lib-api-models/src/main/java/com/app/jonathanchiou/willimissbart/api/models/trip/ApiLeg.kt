package com.app.jonathanchiou.willimissbart.api.models.trip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class ApiLeg(
    @Json(name = "@origin") val origin: String,
    @Json(name = "@destination") val destination: String,
    @Json(name = "@trainHeadStation") internal val trainHeadStation: String,
    @Json(name = "@origTimeDate") val originDate: String,
    @Json(name = "@origTimeMin") val originDateMinutes: String,
    @Json(name = "@destTimeDate") val destinationDate: String,
    @Json(name = "@destTimeMin") val destinationDateMinutes: String
) {

    // This is because BART is sometimes returning "Millbrae (Caltrain Transfer Platform)" instead of "Millbrae"
    val correctedTrainHeadStation =
        if (trainHeadStation == "Millbrae (Caltrain Transfer Platform)") {
            "Millbrae"
        } else {
            trainHeadStation
        }

    val originTimeMinutesEpoch =
        ((DATE_FORMATTER.parse("${originDate} ${originDateMinutes}").time / 60000))

    val destinationTimeMinutesEpoch =
        ((DATE_FORMATTER.parse("${destinationDate} ${destinationDateMinutes}").time / 60000))

    val duration = (destinationTimeMinutesEpoch - originTimeMinutesEpoch).toInt()

    companion object {

        private val DATE_FORMATTER = SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.US)
    }
}
