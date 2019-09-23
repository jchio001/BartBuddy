package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import com.app.jonathanchiou.willimissbart.trips.models.api.Estimate
import kotlinx.android.parcel.Parcelize
import java.time.Duration

sealed class RealTimeLeg: Parcelable {

    @Parcelize
    class Train(
        val origin: String,
        val destination: String,
        val trainHeadStation: String,
        val duration: Int
    ) : RealTimeLeg()

    @Parcelize
    class Wait(
        val station: String,
        val nextTrainHeadStation: String,
        val duration: Int
    ) : RealTimeLeg()
}
