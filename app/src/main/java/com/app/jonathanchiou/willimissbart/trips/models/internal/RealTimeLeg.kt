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

    fun decrement() = when (this) {
        is Train -> copy(duration = duration - 1)
        is Wait -> copy(duration = duration - 1)
    }
}

fun List<RealTimeLeg>.decrement(): List<RealTimeLeg> {
    if (this.isEmpty()) {
        return this
    }

    val firstLeg = this.first()
    return if (firstLeg.duration > 0) {
        val updatedList = ArrayList<RealTimeLeg>(this.size)
        updatedList.add(firstLeg.decrement())
        for (i in 1 until this.size) {
            updatedList.add(this[i])
        }
        updatedList
    } else {
        this.subList(1, this.size)
    }
}
