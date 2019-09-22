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
    @Json(name = "leg") val legs: List<Leg>,
    @Json(name = "@origTimeMin") val originDepartureTime: String
) {

    fun toRealTimeTrip(
        etd: Etd, estimate:
        Estimate,
        nameToAbbreviationMap: Map<String, String>
    ) : RealTimeTrip {
        val firstLeg = legs.first()

        val completeRealTimeLegs = ArrayList<RealTimeLeg>(legs.size)
        completeRealTimeLegs.add(
            RealTimeLeg(
                origin = firstLeg.origin,
                destination = firstLeg.destination,
                trainHeadStation = etd.abbreviation,
                estimate = estimate
            )
        )

        // TODO: Factor in time between legs!
        var currentTravelTime = estimate.minutes + firstLeg.duration
        for (i in 1 until legs.size) {
            val leg = legs[i]

            completeRealTimeLegs.add(
                RealTimeLeg(
                    origin = leg.origin,
                    destination = leg.destination,
                    trainHeadStation = nameToAbbreviationMap[leg.trainHeadStation]!!,
                    estimate = estimate.copy(minutes = currentTravelTime)
                )
            )

            currentTravelTime += leg.duration
        }

        return RealTimeTrip(
            originAbbreviation = origin,
            destinationAbbreviation = destination,
            completeRealTimeLegs = completeRealTimeLegs
        )
    }
}
