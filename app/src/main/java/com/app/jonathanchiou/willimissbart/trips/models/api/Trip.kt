package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Trip(@Json(name = "@origin")
                val origin: String,
                @Json(name = "@destination")
                val destination: String,
                @Json(name = "leg")
                val legs: List<Leg>)