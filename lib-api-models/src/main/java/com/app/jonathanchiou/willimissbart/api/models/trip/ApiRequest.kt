package com.app.jonathanchiou.willimissbart.api.models.trip

import com.app.jonathanchiou.willimissbart.api.models.SingleToList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiRequest(
    @[Json(name = "trip") SingleToList] val trips: List<ApiTrip>
)
