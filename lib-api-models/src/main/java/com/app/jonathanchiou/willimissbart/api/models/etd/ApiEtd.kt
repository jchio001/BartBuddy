package com.app.jonathanchiou.willimissbart.api.models.etd

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ApiEtd(
    @Json(name = "destination") val destination: String,
    @Json(name = "abbreviation") val abbreviation: String,
    // Even though the JSON name is singular, it can return multiple estimates.
    @Json(name = "estimate") val apiEstimates: List<ApiEstimate>
) : Parcelable {

    @IgnoredOnParcel
    val correctedDestination: String =
        if (destination == "SF Airport") {
            "San Francisco International Airport"
        } else {
            destination
        }
}
