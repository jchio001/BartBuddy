package com.app.jonathanchiou.willimissbart.trips

import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip

fun RealTimeTrip.decrement(): RealTimeTrip {
    val firstLegDecremented = realTimeLegs.first().decrement(1)
    return copy(
        realTimeLegs = mutableListOf(firstLegDecremented)
            .apply { realTimeLegs.subList(1, realTimeLegs.size) }
    )
}

fun List<RealTimeTrip>.decrement(): List<RealTimeTrip> {
    return this.map(RealTimeTrip::decrement)
        .filter { it.realTimeLegs.first().duration >= 0 }
}