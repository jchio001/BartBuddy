package com.app.jonathanchiou.willimissbart.api.models.etd

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiEtdStation(
    @Json(name = "name") val name: String,
    @Json(name = "abbr") val abbr: String,
    @Json(name = "etd") val apiEtds: List<ApiEtd> = ArrayList()
)
