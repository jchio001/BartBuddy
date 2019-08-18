package com.app.jonathanchiou.willimissbart.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class StationsViewModelFactory @Inject constructor(private val stationsManager: StationsManager) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == StationsViewModel::class.java) {
            return StationsViewModel(stationsManager) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}