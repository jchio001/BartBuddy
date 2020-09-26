package com.app.jonathanchiou.willimissbart.api.models.bsa

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class ApiBsa(
    @Json(name = "@id") val id: String?,
    @Json(name = "description") val description: ApiCdataClass,
    @Json(name = "posted") val posted: Date?,
    @Json(name = "expires") val expires: Date?
)
