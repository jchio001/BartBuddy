package com.app.jonathan.willimissbart.trips

import com.app.jonathan.willimissbart.apimodels.etd.Estimate
import com.app.jonathan.willimissbart.apimodels.etd.Etd
import com.app.jonathan.willimissbart.apimodels.trip.Trip
import com.app.jonathan.willimissbart.realtimetrip.RealTimeLeg
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTrip
import java.util.*

fun Trip.toRealTimeTrip(
    etd: Etd,
    estimate: Estimate,
    nameToAbbreviationMap: Map<String, String>
): RealTimeTrip {
    val firstLeg = legs.first()
    val realTimeLegs = ArrayList<RealTimeLeg>(legs.size * 2)
    realTimeLegs.add(
        RealTimeLeg.Wait(
            station = firstLeg.origin,
            nextTrainHeadStation = nameToAbbreviationMap.fullNameFromAbbr(firstLeg.trainHeadStation),
            duration = estimate.minutes
        )
    )
    realTimeLegs.add(
        RealTimeLeg.Train(
            origin = firstLeg.origin,
            destination = firstLeg.destination,
            trainHeadStation = etd.abbreviation,
            duration = firstLeg.duration
        )
    )

    var previousDestinationTimeMinutesEpoch = firstLeg.destinationTimeMinutesEpoch
    for (i in 1 until legs.size) {
        val leg = legs[i]
        val legTrainHeadStation = nameToAbbreviationMap.fullNameFromAbbr(leg.trainHeadStation)

        val waitTime = (leg.originTimeMinutesEpoch - previousDestinationTimeMinutesEpoch).toInt()
        realTimeLegs.add(
            RealTimeLeg.Wait(
                station = leg.origin,
                nextTrainHeadStation = legTrainHeadStation,
                duration = waitTime
            )
        )

        realTimeLegs.add(
            RealTimeLeg.Train(
                origin = leg.origin,
                destination = leg.destination,
                trainHeadStation = legTrainHeadStation,
                duration = leg.duration
            )
        )

        previousDestinationTimeMinutesEpoch = leg.destinationTimeMinutesEpoch
    }

    return RealTimeTrip(
        hexColor = estimate.hexColor,
        lastUpdatedTime = System.currentTimeMillis(),
        originAbbreviation = origin,
        destinationAbbreviation = destination,
        realTimeLegs = realTimeLegs
    )
}
