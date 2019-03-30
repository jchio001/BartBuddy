package com.app.jonathanchiou.willimissbart.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BartResponseWrapper<T>(@Json(name = "root")
                          val root: T)