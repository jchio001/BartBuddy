package com.app.jonathanchiou.willimissbart.api.models.trip

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiSchedule(
    @Json(name = "request") val request: ApiRequest
)
