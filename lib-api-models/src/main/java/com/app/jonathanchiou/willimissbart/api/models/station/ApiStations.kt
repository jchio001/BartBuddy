package com.app.jonathanchiou.willimissbart.api.models.station

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiStations(
    @Json(name = "station") val apiStations: List<ApiStation>
)
