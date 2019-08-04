package com.app.jonathanchiou.willimissbart.trips

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathanchiou.willimissbart.api.BartApiModule
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.stations.StationsModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

class TripViewModelFactory(private val stationsManager: StationsManager,
                           private val bartService: BartService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == RealTimeTripViewModel::class.java) {
            return RealTimeTripViewModel(stationsManager, bartService) as T
        } else if (modelClass == RealTimeLegViewModel::class.java) {
            return RealTimeLegViewModel(stationsManager, bartService) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}

@Module(includes = [BartApiModule::class, StationsModule::class])
class TripModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(stationsManager: StationsManager,
                                bartService: BartService): TripViewModelFactory {
        return TripViewModelFactory(stationsManager, bartService)
    }

    @Provides
    @Singleton
    fun getTripManager(sharedPreferences: SharedPreferences): TripManager {
        return TripManager(sharedPreferences)
    }
}