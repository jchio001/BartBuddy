package com.app.jonathanchiou.willimissbart.trips.models.api

import com.app.jonathanchiou.willimissbart.api.SingleToList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Request(@Json(name = "trip")
                   @SingleToList
                   val trips: List<Trip>)