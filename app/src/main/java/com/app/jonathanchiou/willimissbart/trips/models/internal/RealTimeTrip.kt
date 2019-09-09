package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealTimeTrip(
    val originAbbreviation: String,
    val destinationAbbreviation: String,
    val realTimeLegs: List<RealTimeLeg>
) : Parcelable {

    @IgnoredOnParcel
    val isIncomplete = realTimeLegs.isNotEmpty() && realTimeLegs.last() is RealTimeLeg.Incomplete

    fun completeLeg(completeLeg: RealTimeLeg.Complete) : RealTimeTrip {
        val index = realTimeLegs.lastIndex

        if (realTimeLegs[index] is RealTimeLeg.Complete) {
            throw IllegalStateException("Leg at ${index} already complete!")
        }

        return copy(
            realTimeLegs = realTimeLegs.toMutableList()
                .also {
                    it[index] = completeLeg
                }
        )
    }
}
