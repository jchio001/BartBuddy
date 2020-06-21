package com.app.jonathan.willimissbart.stations.models.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ApiStation(
    @Json(name = "name") val name: String,
    @Json(name = "abbr") var abbr: String,
    @Json(name = "gtfs_latitude") val latitude: Double,
    @Json(name = "gtfs_longitude") val longitude: Double,
    @Json(name = "address") val address: String,
    @Json(name = "city") val city: String,
    @Json(name = "county") val county: String,
    @Json(name = "state") val state: String,
    @Json(name = "zipcode") val zipCode: Int = 0
) : Parcelable {

    fun toDbModel() = ApiStation(
        name = name,
        abbr = abbr,
        latitude = latitude,
        longitude = longitude,
        address = address,
        city = city,
        county = county,
        state = state,
        zipCode = zipCode
    )
}
