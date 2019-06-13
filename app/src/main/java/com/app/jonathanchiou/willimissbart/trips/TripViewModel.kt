package com.app.jonathanchiou.willimissbart.trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.api.ApiClient
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapResponse
import com.app.jonathanchiou.willimissbart.utils.models.responseToUiModelStream
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class TripRequestEvent(val originAbbreviation: String,
                       val destinationAbbreviation: String)

class TripViewModel(application: Application): AndroidViewModel(application) {

    val realTimeTripLiveData = MutableLiveData<UiModel<List<Trip>>>()

    private val tripEventSubject = PublishSubject.create<TripRequestEvent>()

    private val disposable: Disposable

    init {
        disposable = tripEventSubject
            .switchMap { tripRequestEvent ->
                ApiClient.INSTANCE.bartService.getDepartures(
                    tripRequestEvent.originAbbreviation,
                    tripRequestEvent.destinationAbbreviation)
                    .map { wrappedTripResponse ->
                       wrappedTripResponse.mapResponse { it.root.schedule.request.trips }
                    }
                    .responseToUiModelStream()
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