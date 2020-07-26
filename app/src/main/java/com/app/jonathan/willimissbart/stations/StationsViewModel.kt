package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.store.StationStore
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StationsViewModel(private val stationStore: StationStore) : ViewModel() {

    val stationsLiveData = MutableLiveData<StationsViewState>()

    private val compositeDisposable = CompositeDisposable()

    fun getStations() {
        compositeDisposable.add(
            stationStore
                .getStations()
                .map { stations ->
                    StationsViewState(
                        showProgressBar = false,
                        showErrorTextView = false,
                        showStationsRecyclerView = true,
                        stations = stations
                    )
                }
                .onErrorReturn { throwable ->
                    StationsViewState(
                        showProgressBar = false,
                        showErrorTextView = false,
                        showStationsRecyclerView = true,
                        throwable = throwable
                    )
                }
                .toObservable()
                .startWith(
                    StationsViewState(
                        showProgressBar = true,
                        showErrorTextView = false,
                        showStationsRecyclerView = false
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
