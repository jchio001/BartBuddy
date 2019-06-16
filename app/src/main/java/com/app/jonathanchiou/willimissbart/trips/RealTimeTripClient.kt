package com.app.jonathanchiou.willimissbart.trips

import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapResponse
import com.app.jonathanchiou.willimissbart.utils.models.responseToTerminalUiModel
import io.reactivex.Observable

class RealTimeTripClient(private val bartService: BartService) {

    fun getEtdsForTrips(vararg trips: Trip): Observable<UiModel<List<RealTimeTrip>>> {
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
                                        it.destination == trip.destination
                                    }
                            )
                        }
                    }
                    .responseToTerminalUiModel()
            }

        return Observable
            .zip(etdObservables) {
                UiModel.zip(*it as Array<UiModel<RealTimeTrip>>)
            }
            .startWith(UiModel(state = State.PENDING))
    }
}