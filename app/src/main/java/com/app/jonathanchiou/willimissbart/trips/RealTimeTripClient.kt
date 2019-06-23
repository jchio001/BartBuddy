package com.app.jonathanchiou.willimissbart.trips

import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapBody
import com.app.jonathanchiou.willimissbart.utils.models.toTerminalUiModelStream
import io.reactivex.Observable

class RealTimeTripClient(private val bartService: BartService) {

    fun getEtdsForTrips(tripRequestEvent: TripRequestEvent,
                        trips: List<Trip>): Observable<UiModel<TripRequestEvent, List<RealTimeTrip>>> {
        val etdObservables = trips
            .map { trip ->
                bartService.getRealTimeEstimates(trip.legs[0].origin)
                    .mapBody { etdRootWrapper ->
                        RealTimeTrip(
                            trip.origin,
                            trip.destination,
                            etdRootWrapper.root.etdStations[0].etds
                                .filter {
                                    it.destination.contains(trip.legs[0].trainHeadStation)
                                }
                        )
                    }
                    .toTerminalUiModelStream(query = tripRequestEvent)
            }

        return Observable
            .zip(etdObservables) { objects ->
                UiModel.zip(objects.map { it as UiModel<TripRequestEvent, RealTimeTrip> })
                    .let { realTimeTripUiModel ->
                        if (realTimeTripUiModel.data != null ) {
                            realTimeTripUiModel.copy(
                                data = realTimeTripUiModel.data
                                    .filter { it.originEtds.isNotEmpty() })
                        } else {
                            realTimeTripUiModel
                        }
                    }
            }
    }
}