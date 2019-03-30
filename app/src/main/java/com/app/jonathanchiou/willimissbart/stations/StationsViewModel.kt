package com.app.jonathanchiou.willimissbart.stations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
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