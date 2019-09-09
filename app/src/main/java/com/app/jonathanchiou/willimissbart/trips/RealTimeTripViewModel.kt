package com.app.jonathanchiou.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapBody
import com.app.jonathanchiou.willimissbart.utils.models.toTerminalUiModelStream
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class TripRequestEvent(
    val originAbbreviation: String,
    val destinationAbbreviation: String
)

class RealTimeTripViewModel(stationsManager: StationsManager,
                            bartService: BartService) : ViewModel() {

    val realTimeTripLiveData = MutableLiveData<UiModel<TripRequestEvent, List<RealTimeTrip>>>()

    private val tripEventSubject = PublishSubject.create<TripRequestEvent>()

    private val disposable: Disposable

    init {
        disposable = tripEventSubject
            .switchMap { tripRequestEvent ->
                bartService.getDepartures(
                    tripRequestEvent.originAbbreviation,
                    tripRequestEvent.destinationAbbreviation)
                    .mapBody { departuresRootsWrapper ->
                        departuresRootsWrapper.root.schedule.request.trips
                            .distinctBy { trip ->
                                trip.legs
                                    .map {
                                        "${it.origin}${it.destination}${it.trainHeadStation}"
                                    }
                                    .reduce { s1, s2 -> "$s1$s2" }
                            }
                    }
                    .flatMap {
                        if (it.isSuccessful) {
                            bartService.getEtdsForTrips(stationsManager, tripRequestEvent, it.body()!!)
                        } else {
                            Observable.just(
                                UiModel(
                                    state = State.ERROR,
                                    query = tripRequestEvent,
                                    statusCode = it.code()))
                        }
                    }
                    .startWith(
                        UiModel(
                            state = State.PENDING,
                            query = tripRequestEvent))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(realTimeTripLiveData::postValue)
    }

    fun requestTrip(originAbbreviation: String,
                    destinationAbbreviation: String) {
        realTimeTripLiveData.value.also {
            if (it == null || it.state == State.ERROR
                || it.query!!.originAbbreviation != originAbbreviation
                || it.query.destinationAbbreviation != destinationAbbreviation
            ) {
                tripEventSubject.onNext(TripRequestEvent(originAbbreviation, destinationAbbreviation))
            }
        }
    }

    fun BartService.getEtdsForTrips(stationsManager: StationsManager,
                                    tripRequestEvent: TripRequestEvent,
                                    trips: List<Trip>):
        Observable<UiModel<TripRequestEvent, List<RealTimeTrip>>> {
        val etdObservables = trips
            .map { trip ->
                this.getRealTimeEstimates(trip.legs[0].origin)
                    .mapBody { etdRootWrapper ->
                        val firstLeg = trip.legs[0]
                        etdRootWrapper.root.etdStations[0].etds
                            .filter {
                                firstLeg.trainHeadStation.contains(it.destination)
                            }
                            .firstOrNull()
                            ?.let { etd ->
                                etd.estimates.map {
                                    val realTimeLegs = ArrayList<RealTimeLeg>(trip.legs.size)

                                    realTimeLegs.add(
                                        RealTimeLeg.Complete(
                                            firstLeg.origin,
                                            firstLeg.destination,
                                            etd.abbreviation,
                                            it
                                        )
                                    )

                                    val stationNameToAbbreivationMap by lazy {
                                        val stations = stationsManager.getStationsFromLocalStorage()

                                        val trainHeadStations = HashSet<String>(trip.legs.size - 1, 1.0f)
                                        for (i in 1 until trip.legs.size) {
                                            trainHeadStations.add(trip.legs[i].trainHeadStation)
                                        }

                                        stations.filter { station -> trainHeadStations.contains(station.name) }
                                            .map { station -> station.name to station.abbr }
                                            .toMap()
                                    }

                                    for (i in 1 until trip.legs.size) {
                                        realTimeLegs.add(
                                            RealTimeLeg.Incomplete(
                                                trip.legs[i].origin,
                                                trip.legs[i].destination,
                                                stationNameToAbbreivationMap[trip.legs[i].trainHeadStation]!!
                                            )
                                        )
                                    }

                                    RealTimeTrip(
                                        trip.origin,
                                        trip.destination,
                                        realTimeLegs)
                                }
                            } as MutableList? ?: mutableListOf()
                    }
                    .toTerminalUiModelStream(query = tripRequestEvent)
            }

        return Observable
            .zip(etdObservables) { objects ->
                UiModel.zipAndFlatten(objects.map { it as UiModel<TripRequestEvent, List<RealTimeTrip>> })
            }
    }
}
