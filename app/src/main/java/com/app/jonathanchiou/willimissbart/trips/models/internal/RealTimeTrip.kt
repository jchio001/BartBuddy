package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RealTimeTrip(
    val originAbbreviation: String,
    val destinationAbbreviation: String,
    val completeRealTimeLegs: List<RealTimeLeg>
) : Parcelable
