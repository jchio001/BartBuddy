package com.app.jonathanchiou.willimissbart.trips.models.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeparturesRoot(@Json(name = "schedule")
                          val schedule: Schedule)