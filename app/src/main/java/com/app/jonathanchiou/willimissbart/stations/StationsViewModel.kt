package com.app.jonathanchiou.willimissbart.stations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathanchiou.willimissbart.stations.models.api.Station
import com.app.jonathanchiou.willimissbart.utils.models.UiModel

class StationsViewModel(private val stationsManager: StationsManager): ViewModel() {

    fun getStationsLiveData(): MutableLiveData<UiModel<Void, List<Station>>> {
        return stationsManager.stationsLiveData
    }

    fun requestStations() {
        stationsManager.requestStations()
    }
}