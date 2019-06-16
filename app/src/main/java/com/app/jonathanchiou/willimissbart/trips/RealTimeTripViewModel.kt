package com.app.jonathanchiou.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class TripRequestEvent(val originAbbreviation: String,
                       val destinationAbbreviation: String)

class RealTimeTripViewModel(bartService: BartService): ViewModel() {

    val realTimeTripLiveData = MutableLiveData<UiModel<List<RealTimeTrip>>>()

    private val tripEventSubject = PublishSubject.create<TripRequestEvent>()

    private val disposable: Disposable

    private val realTimeTripClient = RealTimeTripClient(bartService)

    init {
        disposable = tripEventSubject
            .switchMap { tripRequestEvent ->
                bartService.getDepartures(
                    tripRequestEvent.originAbbreviation,
                    tripRequestEvent.destinationAbbreviation)
                    .map { wrappedTripResponse ->
                       wrappedTripResponse.mapResponse {
                           it.root.schedule.request.trips }
                    }
                    .flatMap {
                        if (it.isSuccessful) {
                            realTimeTripClient.getEtdsForTrips(it.body()!!)
                        } else {
                            Observable.just(
                                UiModel(
                                state = State.ERROR,
                                statusCode = it.code()))
                        }
                    }
                    .startWith(UiModel(state = State.PENDING))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(realTimeTripLiveData::postValue)
    }

    fun requestTrip(originAbbreviation: String,
                    destinationAbbreviation: String) {
        tripEventSubject.onNext(
            TripRequestEvent(originAbbreviation, destinationAbbreviation))
    }
}