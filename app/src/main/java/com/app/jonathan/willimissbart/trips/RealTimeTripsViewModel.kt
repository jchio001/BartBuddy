package com.app.jonathan.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.api.isHandledNetworkException
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.store.StationStore
import com.app.jonathan.willimissbart.trips.models.api.Trip
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathan.willimissbart.trips.models.internal.Union
import com.app.jonathan.willimissbart.trips.models.internal.toRealTimeTrip
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class RealTimeTripViewModel(
    stationStore: StationStore,
    bartService: BartService
) : ViewModel() {

    val realTimeTripLiveData = MutableLiveData<RealTimeTripsViewState>()

    private val tripEventSubject = PublishSubject.create<TripsRequestEvent>()

    private val disposable: Disposable

    init {
        disposable = tripEventSubject
            .distinctUntilChanged()
            .switchMap { tripRequestEvent ->
                bartService
                    .getFilteredTrips(
                        originAbbr = tripRequestEvent.originAbbreviation,
                        destinationAbbr = tripRequestEvent.destinationAbbreviation
                    )
                    .subscribeOn(Schedulers.io())
                    .flatMapObservable { trips ->
                        val stationAbbrs = hashSetOf<String>()
                        val stationNames = hashSetOf<String>()
                        for (trip in trips) {
                            for (leg in trip.legs) {
                                stationAbbrs.add(leg.origin)
                                stationAbbrs.add(leg.destination)
                                stationNames.add(leg.trainHeadStation)
                            }
                        }

                        stationStore
                            .getStationsWithNamesAndAbbrs(
                                abbrs = stationAbbrs.toList(),
                                names = stationNames.toList()
                            )
                            .map { stations ->
                                var abbrCount = 0
                                var nameCount = 0

                                for (station in stations) {
                                    if (station.abbr in stationAbbrs) {
                                        ++abbrCount
                                    }
                                    if (station.name in stationNames) {
                                        ++nameCount
                                    }
                                }

                                check(
                                    abbrCount == stationAbbrs.count() &&
                                        nameCount == stationNames.count()) {
                                    "Stations are stale."
                                }

                                stations
                            }
                            .subscribeOn(Schedulers.io())
                            .flatMapObservable { stations ->
                                bartService.getEtdsForTrips(trips, stations)
                                    .subscribeOn(Schedulers.io())
                                    .flatMap { realTimeTripsViewState ->
                                        Observable.interval(1, BuildConfig.UPDATE_TIME_UNIT)
                                            .scan(realTimeTripsViewState) { previousRealTimeTripsViewState, _ ->
                                                previousRealTimeTripsViewState.copy(
                                                    realTimeTrips = previousRealTimeTripsViewState.realTimeTrips?.decrement()
                                                )
                                            }
                                            .takeUntil { it.realTimeTrips?.isEmpty() ?: true }
                                            .observeOn(AndroidSchedulers.mainThread())
                                    }
                            }
                            .onErrorReturn { throwable ->
                                RealTimeTripsViewState(
                                    showProgressBar = false,
                                    showRecyclerView = false,
                                    throwable = throwable
                                )
                            }
                            .startWith(
                                RealTimeTripsViewState(
                                    showProgressBar = true,
                                    showRecyclerView = false
                                )
                            )
                    }
            }
            .subscribe(realTimeTripLiveData::postValue)
    }

    fun requestTrip(
        originAbbreviation: String,
        destinationAbbreviation: String
    ) {
        tripEventSubject.onNext(TripsRequestEvent(originAbbreviation, destinationAbbreviation))
    }

    private fun BartService.getFilteredTrips(
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

    private fun BartService.getEtdsForTrips(
        trips: List<Trip>,
        stations: List<Station>
    ): Observable<RealTimeTripsViewState> {
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

                if (throwables.isEmpty()) {
                    RealTimeTripsViewState(
                        showProgressBar = false,
                        showRecyclerView = true,
                        realTimeTrips = realTimeTrips as List<RealTimeTrip>
                    )
                } else {
                    RealTimeTripsViewState(
                        showProgressBar = false,
                        showRecyclerView = false,
                        throwable = CompositeException(throwables)
                    )
                }
            }
    }
}
