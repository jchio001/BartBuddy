package com.app.jonathan.willimissbart.apimodels.trip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TripsRoot(
    @Json(name = "schedule") val schedule: Schedule
)