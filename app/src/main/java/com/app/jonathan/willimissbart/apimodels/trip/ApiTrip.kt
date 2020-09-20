package com.app.jonathan.willimissbart.apimodels.trip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiTrip(
    @Json(name = "@origin") val origin: String,
    @Json(name = "@destination") val destination: String,
    @Json(name = "@origTimeMin") val originDepartureTime: String,
    @Json(name = "leg") val legs: List<ApiLeg>
)
