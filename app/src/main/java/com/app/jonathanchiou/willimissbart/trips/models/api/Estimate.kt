package com.app.jonathanchiou.willimissbart.trips.models.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Estimate(@Json(name = "minutes")
                    val minutes: Int,
                    @Json(name= "platform")
                    val platform: Int,
                    @Json(name = "hexcolor")
                    val hexColor: String): Parcelable