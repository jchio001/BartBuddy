package com.app.jonathan.willimissbart.trips.models.api

import com.app.jonathan.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Trip(
    @Json(name = "@origin") val origin: String,
    @Json(name = "@destination") val destination: String,
    @Json(name = "@origTimeMin") val originDepartureTime: String,
    @Json(name = "leg") val legs: List<Leg>
)
