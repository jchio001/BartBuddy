package com.app.jonathanchiou.willimissbart.trips.models.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Etd(@Json(name = "destination")
               val destination: String,
               @Json(name = "abbreviation")
               val abbreviation: String,
               @Json(name = "estimate")
               // Even though the JSON name is singular, it ccan return multiple estimates.
               val estimates: List<Estimate>): Parcelable