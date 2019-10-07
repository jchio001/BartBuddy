package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class RealTimeLeg : Parcelable {

    abstract val duration: Int

    @Parcelize
    data class Train(
        val origin: String,
        val destination: String,
        val trainHeadStation: String,
        override val duration: Int
    ) : RealTimeLeg()

    @Parcelize
    data class Wait(
        val station: String,
        val nextTrainHeadStation: String,
        override val duration: Int
    ) : RealTimeLeg()

    fun decrement(interval: Int) = when (this) {
        is Train -> copy(duration = duration - interval)
        is Wait -> copy(duration = duration - interval)
    }
}

fun List<RealTimeLeg>.decrement(duration: Int): List<RealTimeLeg> {
    if (this.isEmpty()) {
        return this
    }

    val firstLeg = this.first()
    val updatedFirstLegDuration = firstLeg.duration - duration
    return if (updatedFirstLegDuration >= 0) {
        val updatedList = ArrayList<RealTimeLeg>(this.size)
        updatedList.add(firstLeg.decrement(duration))
        for (i in 1 until this.size) {
            updatedList.add(this[i])
        }
        updatedList
    } else if (updatedFirstLegDuration == -1) {
        return this.subList(1, this.size)
    } else {
        return this.subList(1, this.size).decrement(updatedFirstLegDuration * -1)
    }
}
