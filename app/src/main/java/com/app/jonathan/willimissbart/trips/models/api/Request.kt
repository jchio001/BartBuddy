package com.app.jonathan.willimissbart.trips.models.api

import com.app.jonathan.willimissbart.api.SingleToList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Request(
    @[Json(name = "trip") SingleToList] val trips: List<Trip>
)
