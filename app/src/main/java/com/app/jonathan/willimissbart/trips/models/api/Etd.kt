package com.app.jonathan.willimissbart.trips.models.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Etd(
    @Json(name = "destination") val destination: String,
    @Json(name = "abbreviation") val abbreviation: String,
    // Even though the JSON name is singular, it can return multiple estimates.
    @Json(name = "estimate") val estimates: List<Estimate>
) : Parcelable
