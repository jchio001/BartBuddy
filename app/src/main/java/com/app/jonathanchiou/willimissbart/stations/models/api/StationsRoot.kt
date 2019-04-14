package com.app.jonathanchiou.willimissbart.stations.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StationsRoot(@Json(name = "stations")
                        val stations: Stations)