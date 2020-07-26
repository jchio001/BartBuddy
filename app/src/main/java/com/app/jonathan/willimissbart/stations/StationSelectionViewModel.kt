package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.store.StationStore
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StationSelectionViewModel(private val stationStore: StationStore) : ViewModel() {

    val stationsLiveData = MutableLiveData<StationSelectionViewState>()

    private val compositeDisposable = CompositeDisposable()

    fun getStations() {
        compositeDisposable.add(
            stationStore
                .getStations()
                .map { stations ->
                    StationSelectionViewState(
                        showProgressBar = false,
                        showErrorTextView = false,
                        showStationsRecyclerView = true,
                        stations = stations
                    )
                }
                .onErrorReturn { throwable ->
                    StationSelectionViewState(
                        showProgressBar = false,
                        showErrorTextView = false,
                        showStationsRecyclerView = true,
                        throwable = throwable
                    )
                }
                .toObservable()
                .startWith(
                    StationSelectionViewState(
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
