package com.app.jonathan.willimissbart.trips

import com.app.jonathanchiou.willimissbart.api.models.etd.ApiEstimate
import com.app.jonathanchiou.willimissbart.api.models.etd.ApiEtd
import com.app.jonathanchiou.willimissbart.api.models.trip.ApiTrip
import com.app.jonathan.willimissbart.realtimetrip.RealTimeLeg
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTrip
import java.util.*

fun ApiTrip.toRealTimeTrip(
    apiEtd: ApiEtd,
    apiEstimate: ApiEstimate,
    nameToAbbreviationMap: Map<String, String>
): RealTimeTrip {
    val firstLeg = legs.first()
    val realTimeLegs = ArrayList<RealTimeLeg>(legs.size * 2)
    realTimeLegs.add(
        RealTimeLeg.Wait(
            station = firstLeg.origin,
            nextTrainHeadStation = nameToAbbreviationMap.fullNameFromAbbr(firstLeg.correctedTrainHeadStation),
            duration = apiEstimate.minutes
        )
    )
    realTimeLegs.add(
        RealTimeLeg.Train(
            origin = firstLeg.origin,
            destination = firstLeg.destination,
            trainHeadStation = apiEtd.abbreviation,
            duration = firstLeg.duration
        )
    )

    var previousDestinationTimeMinutesEpoch = firstLeg.destinationTimeMinutesEpoch
    for (i in 1 until legs.size) {
        val leg = legs[i]
        val legTrainHeadStation = nameToAbbreviationMap.fullNameFromAbbr(leg.correctedTrainHeadStation)

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
        hexColor = apiEstimate.hexColor,
        lastUpdatedTime = System.currentTimeMillis(),
        originAbbreviation = origin,
        destinationAbbreviation = destination,
        realTimeLegs = realTimeLegs
    )
}
