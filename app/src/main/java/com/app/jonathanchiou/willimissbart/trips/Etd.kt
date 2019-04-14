package com.app.jonathanchiou.willimissbart.trips

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Etd(@Json(name = "destination")
               val destination: String,
               @Json(name = "abbreviation")
               val abbreviation: String,
               @Json(name = "estimate")
               // Even though the JSON name is singular, it ccan return multiple estimates.
               val estimates: List<Estimate>)