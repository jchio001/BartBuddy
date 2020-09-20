package com.app.jonathan.willimissbart.apimodels.trip

import com.app.jonathan.willimissbart.api.SingleToList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiRequest(
    @[Json(name = "trip") SingleToList] val trips: List<ApiTrip>
)
