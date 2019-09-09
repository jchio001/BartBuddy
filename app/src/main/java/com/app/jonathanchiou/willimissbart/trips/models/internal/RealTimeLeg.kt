package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import com.app.jonathanchiou.willimissbart.trips.models.api.Estimate
import kotlinx.android.parcel.Parcelize

sealed class RealTimeLeg : Parcelable {

    @Parcelize
    class Complete(
        val origin: String,
        val destination: String,
        val trainHeadStation: String,
        val estimate: Estimate
    ) : RealTimeLeg()

    @Parcelize
    class Incomplete(
        val origin: String,
        val destination: String,
        val trainHeadStation: String
    ) : RealTimeLeg()
}
