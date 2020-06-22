package com.app.jonathan.willimissbart.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Station(
    val name: String,
    @PrimaryKey val abbr: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val county: String,
    val state: String,
    val zipCode: Int
): Parcelable
