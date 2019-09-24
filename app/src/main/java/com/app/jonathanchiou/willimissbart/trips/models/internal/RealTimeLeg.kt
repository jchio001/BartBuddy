package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import com.app.jonathanchiou.willimissbart.trips.models.api.Estimate
import kotlinx.android.parcel.Parcelize
import java.time.Duration

sealed class RealTimeLeg: Parcelable {

    abstract val duration: Int

    @Parcelize
    class Train(
        val origin: String,
        val destination: String,
        val trainHeadStation: String,
        override val duration: Int
    ) : RealTimeLeg()

    @Parcelize
    class Wait(
        val station: String,
        val nextTrainHeadStation: String,
        override val duration: Int
    ) : RealTimeLeg()
}
