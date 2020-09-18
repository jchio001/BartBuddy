package com.app.jonathan.willimissbart.realtimetrip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.store.StationStore
import com.app.jonathan.willimissbart.trips.TripsRequestEvent
import com.app.jonathan.willimissbart.trips.decrement
import com.app.jonathan.willimissbart.trips.getEtdsForTrips
import com.app.jonathan.willimissbart.trips.getFilteredTrips
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
                                stationAbbrs = stationAbbrs.toList(),
                                stationNames = stationNames.toList()
                            )
                            .flatMapObservable { stations ->
                                bartService.getEtdsForTrips(trips, stations)
                                    .subscribeOn(Schedulers.io())
                                    .flatMap { realTimeTripsViewState ->
                                        Observable.interval(1, BuildConfig.UPDATE_TIME_UNIT)
                                            .scan(realTimeTripsViewState) { previousRealTimeTrips, _ ->
                                                previousRealTimeTrips.decrement()
                                            }
                                            .takeUntil { it.isEmpty() }
                                            .observeOn(AndroidSchedulers.mainThread())
                                    }
                                    .map { realTimeTrips ->
                                        RealTimeTripsViewState(
                                            showProgressBar = false,
                                            showRecyclerView = true,
                                            realTimeTrips = realTimeTrips
                                        )
                                    }
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
            .subscribe(realTimeTripLiveData::postValue)
    }

    fun requestTrip(
        originAbbreviation: String,
        destinationAbbreviation: String
    ) {
        tripEventSubject.onNext(TripsRequestEvent(originAbbreviation, destinationAbbreviation))
    }
}
