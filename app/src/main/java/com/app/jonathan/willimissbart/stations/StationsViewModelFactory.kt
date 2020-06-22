package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathan.willimissbart.store.StationStore
import javax.inject.Inject

class StationsViewModelFactory @Inject constructor(private val stationStore: StationStore) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == StationsViewModel::class.java) {
            return StationsViewModel(stationStore) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}
