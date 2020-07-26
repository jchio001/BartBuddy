package com.app.jonathan.willimissbart.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathan.willimissbart.store.StationStore
import javax.inject.Inject

class StationSelectionViewModelFactory @Inject constructor(private val stationStore: StationStore) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == StationSelectionViewModel::class.java) {
            return StationSelectionViewModel(stationStore) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}
