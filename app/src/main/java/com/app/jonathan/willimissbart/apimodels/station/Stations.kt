package com.app.jonathan.willimissbart.apimodels.station

import com.app.jonathan.willimissbart.apimodels.station.ApiStation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stations(
    @Json(name = "station") val apiStations: List<ApiStation>
)
