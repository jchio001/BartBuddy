package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.stations.models.api.ApiStation
import com.app.jonathan.willimissbart.store.StationStore
import com.app.jonathan.willimissbart.utils.models.State
import com.app.jonathan.willimissbart.utils.models.UiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StationsViewModel(private val stationStore: StationStore) : ViewModel() {

    val stationsLiveData = MutableLiveData<UiModel<Void, List<Station>>>()

    private val compositeDisposable = CompositeDisposable()

    fun getStations() {
        stationStore.refresh(false)

        compositeDisposable.add(
            stationStore
                .stream()
                .timeout(45, TimeUnit.SECONDS)
                .filter(List<Station>::isNotEmpty)
                .takeUntil(List<Station>::isNotEmpty)
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
