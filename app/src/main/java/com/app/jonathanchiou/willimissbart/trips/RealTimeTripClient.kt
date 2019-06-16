package com.app.jonathanchiou.willimissbart.trips

import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapResponse
import com.app.jonathanchiou.willimissbart.utils.models.toTerminalUiModelStream
import io.reactivex.Observable

class RealTimeTripClient(private val bartService: BartService) {

    fun getEtdsForTrips(trips: List<Trip>): Observable<UiModel<List<RealTimeTrip>>> {
        val etdObservables = trips
            .map { trip ->
                bartService.getRealTimeEstimates(trip.legs[0].origin)
                    .map { response ->
                        response.mapResponse { etdRootWrapper ->
                            RealTimeTrip(
                                trip.origin,
                                trip.destination,
                                etdRootWrapper.root.etdStations[0].etds
                                    .filter {
                                        it.destination.contains(trip.legs[0].trainHeadStation)
                                    }
                            )
                        }
                    }
                    .toTerminalUiModelStream()
            }



        return Observable
            .zip(etdObservables) { objects ->
                UiModel.zip(
                    objects
                        .map { it as UiModel<RealTimeTrip> })
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