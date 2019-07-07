package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealTimeTrip(val originAbbreviation: String,
                        val destinationAbbreviation: String,
                        val realTimeLegs: MutableList<RealTimeLeg>): Parcelable