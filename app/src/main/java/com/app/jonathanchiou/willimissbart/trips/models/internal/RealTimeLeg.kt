package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import com.app.jonathanchiou.willimissbart.trips.models.api.Etd
import com.app.jonathanchiou.willimissbart.utils.models.State
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealTimeLeg(val state: State,
                       val origin: String,
                       val destination: String,
                       val trainHeadStation: String,
                       val etds: MutableList<Etd>): Parcelable