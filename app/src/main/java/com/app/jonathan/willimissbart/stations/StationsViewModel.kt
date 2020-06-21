package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.stations.models.api.ApiStation
import com.app.jonathan.willimissbart.utils.models.UiModel

class StationsViewModel(private val stationsManager: StationsManager) : ViewModel() {

    fun getStationsLiveData(): MutableLiveData<UiModel<Void, List<ApiStation>>> {
        return stationsManager.stationsLiveData
    }

    fun requestStations() {
        stationsManager.requestStations()
    }
}
