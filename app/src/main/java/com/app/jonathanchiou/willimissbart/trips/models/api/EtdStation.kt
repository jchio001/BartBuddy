package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EtdStation(@Json(name = "name")
                      val name: String,
                      val abbr: String,
                      val etds: List<Etd>)