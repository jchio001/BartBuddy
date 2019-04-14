package com.app.jonathanchiou.willimissbart.trips

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Estimate(@Json(name = "minutes")
                    val minutes: Int,
                    @Json(name= "platform")
                    val platform: Int)