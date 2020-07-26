package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.store.StationStore
import com.app.jonathan.willimissbart.utils.models.State
import com.app.jonathan.willimissbart.utils.models.UiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StationsViewModel(private val stationStore: StationStore) : ViewModel() {

    val stationsLiveData = MutableLiveData<UiModel<Void, List<Station>>>()

    private val compositeDisposable = CompositeDisposable()

    fun getStations() {
        compositeDisposable.add(
            stationStore
                .getStations()
                .map { stations ->
                    UiModel<Void, List<Station>>(
                        state = State.DONE,
                        data = stations
                    )
                }
                .onErrorReturn { throwable ->
                    UiModel(
                        state = State.ERROR,
                        error = throwable
                    )
                }
                .toObservable()
                .startWith(
                    UiModel(
                        state = State.PENDING
                    )
                )
                .subscribeOn(Schedulers.io())
                .subscribe(stationsLiveData::postValue)
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
