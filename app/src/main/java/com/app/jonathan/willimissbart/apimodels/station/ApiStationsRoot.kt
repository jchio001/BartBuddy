package com.app.jonathan.willimissbart.apimodels.station

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiStationsRoot(
    @Json(name = "stations") val stations: ApiStations
)
