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

    val indexOfActiveLeg = this.indexOfFirst { it.duration >= 0 }
    if (indexOfActiveLeg != -1) {
        val updatedList = ArrayList<RealTimeLeg>(this.size)
        updatedList.addAll(this.subList(0, indexOfActiveLeg))
        updatedList.add(this[indexOfActiveLeg].decrement(duration))
        updatedList.addAll(this.subList(indexOfActiveLeg + 1, this.size))
        return updatedList
    } else {
        return this
    }
}
