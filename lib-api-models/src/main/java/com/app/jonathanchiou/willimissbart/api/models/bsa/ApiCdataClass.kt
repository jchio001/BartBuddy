package com.app.jonathanchiou.willimissbart.api.models.bsa

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApiCdataClass(
    @Json(name = "#cdata-section") val cDataSection: String
)
