package com.app.jonathanchiou.willimissbart.stations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import io.reactivex.subjects.PublishSubject

class StationsViewModel(application: Application): AndroidViewModel(application) {

    private val stationsManager = StationsManager.get()

    fun getStationsLiveData(): MutableLiveData<UiModel<List<Station>>> {
        return stationsManager.stationsLiveData
    }

    fun requestStations() {
        stationsManager.requestStations()
    }
}