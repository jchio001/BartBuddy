package com.app.jonathan.willimissbart.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.store.StationStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripViewModelFactory @Inject constructor(
    private val stationsStore: StationStore,
    private val bartService: BartService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == RealTimeTripViewModel::class.java) {
            return RealTimeTripViewModel(stationsStore, bartService) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}
