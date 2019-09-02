package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EtdRoot(
    // For some reason, this is always a list of 1.
    @Json(name = "station") val etdStations: List<EtdStation>
)
