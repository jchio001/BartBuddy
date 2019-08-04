package com.app.jonathanchiou.willimissbart.stations

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathanchiou.willimissbart.api.BartApiModule
import com.app.jonathanchiou.willimissbart.api.BartService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

class StationsViewModelFactory(private val stationsManager: StationsManager):
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == StationsViewModel::class.java) {
            return StationsViewModel(stationsManager) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}

@Module(includes = [BartApiModule::class])
class StationsModule {

    @Provides
    @Singleton
    fun getStationsManger(
        sharedPreferences: SharedPreferences,
        bartService: BartService,
        moshi: Moshi): StationsManager {
        return StationsManager(sharedPreferences, bartService, moshi)
    }

    @Provides
    fun getStationViewModelFactory(stationsManager: StationsManager): StationsViewModelFactory {
        return StationsViewModelFactory(stationsManager)
    }
}