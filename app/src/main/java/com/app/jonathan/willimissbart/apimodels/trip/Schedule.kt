package com.app.jonathan.willimissbart.apimodels.trip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Schedule(
    @Json(name = "request") val request: Request
)