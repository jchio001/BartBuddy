package com.app.jonathanchiou.willimissbart.stations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.stations.models.api.Station
import com.app.jonathanchiou.willimissbart.utils.models.UiModel

class StationsViewModel(application: Application): AndroidViewModel(application) {

    private val stationsManager = StationsManager.get()

    fun getStationsLiveData(): MutableLiveData<UiModel<Void, List<Station>>> {
        return stationsManager.stationsLiveData
    }

    fun requestStations() {
        stationsManager.requestStations()
    }
}