package com.app.jonathanchiou.willimissbart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class StationsViewModel(application: Application): AndroidViewModel(application) {

    val stationsLiveData =  MutableLiveData<UiModel<List<Station>>>()

    private val stationsRequestSubject = PublishSubject.create<Any>()

    init {
        stationsRequestSubject
            .switchMap {
                ApiClient.INSTANCE
                    .bartService
                    .getStations()
                    .toUiModelObservable()
                    .map {
                        if (it.state == State.DONE) {
                            UiModel(
                                state = it.state,
                                data = it.data!!.root.stations.stations)
                        } else {
                            UiModel(state = it.state)
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(stationsLiveData::postValue)
    }

    fun requestStations() {
        stationsRequestSubject.onNext(Any())
    }
}