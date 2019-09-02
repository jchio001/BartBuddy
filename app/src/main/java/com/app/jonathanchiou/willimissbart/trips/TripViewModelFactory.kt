package com.app.jonathanchiou.willimissbart.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripViewModelFactory @Inject constructor(
    private val stationsManager: StationsManager,
    private val bartService: BartService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == RealTimeTripViewModel::class.java) {
            return RealTimeTripViewModel(stationsManager, bartService) as T
        } else if (modelClass == RealTimeLegViewModel::class.java) {
            return RealTimeLegViewModel(stationsManager, bartService) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}
