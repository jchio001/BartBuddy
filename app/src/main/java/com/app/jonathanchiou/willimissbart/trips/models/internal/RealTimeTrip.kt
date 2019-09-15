package com.app.jonathanchiou.willimissbart.trips.models.internal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class RealTimeTrip : Parcelable {

    abstract val originAbbreviation: String
    abstract val destinationAbbreviation: String
    abstract val completeRealTimeLegs: List<RealTimeLeg.Complete>

    @Parcelize
    class Complete(
        override val originAbbreviation: String,
        override val destinationAbbreviation: String,
        override val completeRealTimeLegs: List<RealTimeLeg.Complete>
    ) : RealTimeTrip()

    @Parcelize
    class Incomplete(
        override val originAbbreviation: String,
        override val destinationAbbreviation: String,
        override val completeRealTimeLegs: List<RealTimeLeg.Complete>,
        val incompleteRealTimeLegs: List<RealTimeLeg.Incomplete>
    ) : RealTimeTrip() {

        fun complete(vararg completeRealTimeLegs: RealTimeLeg.Complete) : RealTimeTrip.Complete {
            if (incompleteRealTimeLegs.size != completeRealTimeLegs.size) {
                throw IllegalStateException("Failed to complete real time trip!")
            }

            return RealTimeTrip.Complete(
                originAbbreviation = originAbbreviation,
                destinationAbbreviation = destinationAbbreviation,
                completeRealTimeLegs = this.completeRealTimeLegs.toMutableList().apply { addAll(completeRealTimeLegs)}
            )
        }
    }
}
