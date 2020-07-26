package com.app.jonathan.willimissbart.trips

import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTrip

fun RealTimeTrip.decrement(): RealTimeTrip {
    val firstLegDecremented = realTimeLegs.first().decrement(1)
    return copy(
        lastUpdatedTime = lastUpdatedTime + BuildConfig.UPDATE_TIME_UNIT.toMillis(1),
        realTimeLegs = mutableListOf(firstLegDecremented)
            .apply { realTimeLegs.subList(1, realTimeLegs.size) }
    )
}

fun List<RealTimeTrip>.decrement(): List<RealTimeTrip> {
    return this.map(RealTimeTrip::decrement)
        .filter { it.realTimeLegs.first().duration >= 0 }
}
