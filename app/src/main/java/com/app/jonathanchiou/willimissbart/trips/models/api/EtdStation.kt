package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EtdStation(@Json(name = "name")
                      val name: String,
                      @Json(name = "abbr")
                      val abbr: String,
                      @Json(name = "etd")
                      val etds: List<Etd> = ArrayList())