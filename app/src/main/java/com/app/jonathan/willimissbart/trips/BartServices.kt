package com.app.jonathan.willimissbart.trips

import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.api.isHandledNetworkException
import com.app.jonathan.willimissbart.apimodels.trip.Trip
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTrip
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.exceptions.CompositeException

internal fun BartService.getFilteredTrips(
    originAbbr: String,
    destinationAbbr: String
): Single<List<Trip>> {
    return this.getDepartures(
        orig = originAbbr,
        dest = destinationAbbr
    )
        .map { wrappedDeparturesRoot ->
            wrappedDeparturesRoot.root.schedule.request.trips
                .distinctBy { trip ->
                    trip.legs
                        .map { leg ->
                            "${leg.origin}${leg.destination}${leg.trainHeadStation}"
                        }
                        .reduce { s1, s2 -> "$s1$s2" }
                }
        }
}

internal fun BartService.getEtdsForTrips(
    trips: List<Trip>,
    stations: List<Station>
): Observable<List<RealTimeTrip>> {
    val etdObservables = trips
        .map { trip ->
            this.getRealTimeEstimates(trip.legs.first().origin)
                .map { etdRootWrapper ->
                    val stationNameToAbbreviationMap by lazy {

                        val trainHeadStations = HashSet<String>(trip.legs.size, 1.0f)
                        for (leg in trip.legs) {
                            trainHeadStations.add(leg.trainHeadStation)
                        }

                        stations.filter { station -> trainHeadStations.contains(station.name) }
                            .map { station -> station.name to station.abbr }
                            .toMap()
                    }

                    val firstLeg = trip.legs.first()
                    val realTimeTrip = etdRootWrapper.root.etdStations.first().etds
                        .firstOrNull { etd ->
                            firstLeg.trainHeadStation.contains(etd.correctedDestination)
                        }
                        ?.let { etd ->
                            etd.estimates.map { estimate ->
                                trip.toRealTimeTrip(etd, estimate, stationNameToAbbreviationMap)
                            }
                        } as MutableList? ?: mutableListOf()
                    Union.first<List<RealTimeTrip>, Throwable>(realTimeTrip)
                }
                .onErrorReturn { Union.second(it) }
        }

    return Observable
        .zip(etdObservables) { objects ->
            val realTimeTrips = mutableListOf<RealTimeTrip>()
            val throwables = mutableListOf<Throwable>()
            for (`object` in objects) {
                when (val union = `object` as Union<List<RealTimeTrip>, Throwable>) {
                    is Union.First -> realTimeTrips.addAll(union.value)
                    is Union.Second -> {
                        if (!union.value.isHandledNetworkException()) {
                            throw union.value
                        }

                        throwables.add(union.value)
                    }
                }
            }

            if (!throwables.isEmpty()) {
                throw CompositeException(throwables)
            }

            realTimeTrips
        }
}
