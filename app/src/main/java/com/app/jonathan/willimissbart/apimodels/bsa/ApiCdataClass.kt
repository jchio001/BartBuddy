package com.app.jonathan.willimissbart.apimodels.bsa

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApiCdataClass(
    @Json(name = "#cdata-section") val cDataSection: String
)
