package com.app.jonathan.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.api.isHandledNetworkException
import com.app.jonathan.willimissbart.stations.StationsManager
import com.app.jonathan.willimissbart.trips.models.api.Trip
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathan.willimissbart.trips.models.internal.Union
import com.app.jonathan.willimissbart.utils.models.State
import com.app.jonathan.willimissbart.utils.models.UiModel
import com.app.jonathan.willimissbart.utils.models.errorToUiModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class TripsRequestEvent(
    val originAbbreviation: String,
    val destinationAbbreviation: String
)

class RealTimeTripViewModel(
    stationsManager: StationsManager,
    bartService: BartService
) : ViewModel() {

    val realTimeTripLiveData = MutableLiveData<UiModel<TripsRequestEvent, List<RealTimeTrip>>>()

    private val tripEventSubject = PublishSubject.create<TripsRequestEvent>()

    private val disposable: Disposable

    init {
        disposable = tripEventSubject
            .switchMap { tripRequestEvent ->
                bartService.getDepartures(
                    tripRequestEvent.originAbbreviation,
                    tripRequestEvent.destinationAbbreviation
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
                    .flatMap { trips ->
                        bartService.getEtdsForTrips(stationsManager, tripRequestEvent, trips)
                            .flatMap { realTimeTripsUiModel ->
                                Observable.interval(1, BuildConfig.UPDATE_TIME_UNIT)
                                    .scan(realTimeTripsUiModel) { realTimeTripsUiModel, _ ->
                                        realTimeTripsUiModel.copy(
                                            data = realTimeTripsUiModel.data?.decrement()
                                        )
                                    }
                                    .takeUntil { it.data?.isEmpty() ?: true }
                            }
                    }
                    .errorToUiModel()
                    .startWith(
                        UiModel(
                            state = State.PENDING,
                            query = tripRequestEvent
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(realTimeTripLiveData::postValue)
    }

    fun requestTrip(
        originAbbreviation: String,
        destinationAbbreviation: String
    ) {
        realTimeTripLiveData.value.also {
            if (it == null || it.state == State.ERROR
                || it.query!!.originAbbreviation != originAbbreviation
                || it.query.destinationAbbreviation != destinationAbbreviation
            ) {
                tripEventSubject.onNext(TripsRequestEvent(originAbbreviation, destinationAbbreviation))
            }
        }
    }

    private fun BartService.getEtdsForTrips(
        stationsManager: StationsManager,
        tripsRequestEvent: TripsRequestEvent,
        trips: List<Trip>
    ): Observable<UiModel<TripsRequestEvent, List<RealTimeTrip>>> {
        val etdObservables = trips
            .map { trip ->
                this.getRealTimeEstimates(trip.legs[0].origin)
                    .map { etdRootWrapper ->
                        val stationNameToAbbreviationMap by lazy {
                            val stations = stationsManager.getStationsFromLocalStorage()

                            val trainHeadStations = HashSet<String>(trip.legs.size, 1.0f)
                            for (leg in trip.legs) {
                                trainHeadStations.add(leg.trainHeadStation)
                            }

                            stations.filter { station -> trainHeadStations.contains(station.name) }
                                .map { station -> station.name to station.abbr }
                                .toMap()
                        }

                        val firstLeg = trip.legs[0]
                        val realTimeTrip = etdRootWrapper.root.etdStations[0].etds
                            .firstOrNull { etd ->
                                firstLeg.trainHeadStation.contains(etd.destination)
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

                if (throwables.isEmpty()) {
                    UiModel(
                        state = State.DONE,
                        query = tripsRequestEvent,
                        data = realTimeTrips as List<RealTimeTrip>
                    )
                } else {
                    UiModel(
                        state = State.ERROR,
                        error = CompositeException(throwables)
                    )
                }
            }
    }
}
