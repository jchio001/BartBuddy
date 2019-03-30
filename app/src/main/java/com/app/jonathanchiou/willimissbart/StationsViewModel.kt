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

    private val stationsManager = StationsManager.get()

    init {
        stationsRequestSubject
            .switchMap {
                stationsManager.getStations()
            }
            .subscribe(stationsLiveData::postValue)
    }

    fun requestStations() {
        stationsLiveData.value.also {
            if (it == null || it.state == State.ERROR) {
                stationsRequestSubject.onNext(Any())
            }
        }
    }
}