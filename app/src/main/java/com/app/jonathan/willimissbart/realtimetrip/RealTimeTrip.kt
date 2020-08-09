package com.app.jonathan.willimissbart.realtimetrip

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealTimeTrip(
    val hexColor: String,
    val lastUpdatedTime: Long,
    val originAbbreviation: String,
    val destinationAbbreviation: String,
    val realTimeLegs: List<RealTimeLeg>
) : Parcelable
