package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EtdRoot(@Json(name = "station")
                   // For some reason, this is always a list of 1.
                   val etdStations: List<EtdStation>)