package com.app.jonathanchiou.willimissbart.trips

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Trip(@Json(name = "trip")
                val trips: List<Trip>)