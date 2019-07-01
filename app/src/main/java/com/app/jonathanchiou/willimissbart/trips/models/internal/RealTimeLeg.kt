package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import com.app.jonathanchiou.willimissbart.trips.models.api.Etd
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealTimeLeg(val origin: String,
                       val destination: String,
                       val trainHeadStation: String,
                       val etds: List<Etd>): Parcelable