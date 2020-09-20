package com.app.jonathanchiou.willimissbart.api.models.bsa

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ApiBsaRoot(
    @Json(name = "bsa") val bsa: List<ApiBsa>
)
