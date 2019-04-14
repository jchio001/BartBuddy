package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Leg(@Json(name = "@originAbbreviation")
               val origin: String,
               @Json(name = "@destination")
               val destination: String,
               @Json(name = "@trainHeadStation")
               val trainHeadStation: String)