package com.app.jonathanchiou.willimissbart.trips.models.api

import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Trip(
    @Json(name = "@origin") val origin: String,
    @Json(name = "@destination") val destination: String,
    @Json(name = "@origTimeMin") val originDepartureTime: String,
    @Json(name = "leg") val legs: List<Leg>
) {

    fun toRealTimeTrip(
        etd: Etd,
        estimate: Estimate,
        nameToAbbreviationMap: Map<String, String>
    ): RealTimeTrip {
        val firstLeg = legs.first()
        val realTimeLegs = ArrayList<RealTimeLeg>(legs.size * 2)
        realTimeLegs.add(
            RealTimeLeg.Wait(
                station = firstLeg.origin,
                nextTrainHeadStation = nameToAbbreviationMap[firstLeg.trainHeadStation]!!,
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
            val legTrainHeadStation = nameToAbbreviationMap[leg.trainHeadStation]!!

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
            originAbbreviation = origin,
            destinationAbbreviation = destination,
            realTimeLegs = realTimeLegs
        )
    }
}
