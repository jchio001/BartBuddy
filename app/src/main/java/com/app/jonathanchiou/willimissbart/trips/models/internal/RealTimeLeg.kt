package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import com.app.jonathanchiou.willimissbart.trips.models.api.Estimate
import kotlinx.android.parcel.Parcelize

@Parcelize
class RealTimeLeg(
    val origin: String,
    val destination: String,
    val trainHeadStation: String,
    val estimate: Estimate
) : Parcelable
