package com.app.jonathanchiou.willimissbart.trips

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.app.jonathanchiou.willimissbart.stations.StationSelectionActivity
import com.app.jonathanchiou.willimissbart.stations.models.api.Station

enum class StationType {
    ORIGIN,
    DESTINATION
}

class TripManager(private val sharedPreferences: SharedPreferences) {

    interface StationListener {
        fun onTripStationChanged(originAbbreviation: String?, destinationAbbreviation: String?)
    }

    var originAbbreviation: String? = null
        private set

    var destinationAbbreviation: String? = null
        private set

    private var stationListeners = HashSet<StationListener>(2)

    init {
        sharedPreferences.also {
            originAbbreviation = it.getString(TRIP_ORIGIN_ABBREVIATION_KEY, null)
            destinationAbbreviation = it.getString(TRIP_DESTINATION_ABBREVIATION_KEY, null)
        }
    }

    fun addListener(stationListener: StationListener) {
        if (!stationListeners.contains(stationListener)) {
            stationListeners.add(stationListener)
            stationListener.onTripStationChanged(originAbbreviation, destinationAbbreviation)
        }
    }

    fun removeListener(stationListener: StationListener) {
        stationListeners.remove(stationListener)
    }

    fun updateStation(fragment: Fragment, stationType: StationType) {
        val intent = Intent(fragment.context, StationSelectionActivity::class.java)
        intent.putExtra(STATION_SELECTION_TYPE_KEY, stationType.toString())
        fragment.startActivityForResult(intent,
                                        STATIONS_SELECTION_CODE)
    }

    fun swapTripStations() {
        val placeholder = originAbbreviation
        originAbbreviation = destinationAbbreviation
        destinationAbbreviation = placeholder

        for (stationListener in stationListeners) {
            stationListener.also {
                it.onTripStationChanged(originAbbreviation, destinationAbbreviation)
            }
        }
    }

    fun onStationSelectionResult(requestCode: Int,
                                 resultCode: Int,
                                 data: Intent?) {
        if (requestCode == STATIONS_SELECTION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val stationType = StationType.valueOf(data!!.getStringExtra(
                    STATION_SELECTION_TYPE_KEY))

                 data.getParcelableExtra<Station>(SELECTED_STATION_KEY).abbr.also {
                     if (stationType == StationType.ORIGIN) {
                         originAbbreviation = it
                     } else {
                         destinationAbbreviation = it
                     }

                     for (stationListener in stationListeners) {
                         stationListener.also {
                             it.onTripStationChanged(originAbbreviation, destinationAbbreviation)
                         }
                     }
                 }
            }
        }
    }

    fun displayTripsFragment(fragment: Fragment, containerId: Int) {
        if (originAbbreviation != null && destinationAbbreviation != null) {
            sharedPreferences
                .edit()
                .putString(TRIP_ORIGIN_ABBREVIATION_KEY, originAbbreviation)
                .putString(TRIP_DESTINATION_ABBREVIATION_KEY, destinationAbbreviation)
                .apply()

            fragment.fragmentManager!!.also {
                it.popBackStack()
                it.beginTransaction()
                    .replace(containerId, TripFragment())
                    .commit()
            }
        }
    }

    companion object {
        const val STATIONS_SELECTION_CODE = 73

        const val STATION_SELECTION_TYPE_KEY = "station_selection_type"
        const val SELECTED_STATION_KEY = "selected_station"

        const val TRIP_ORIGIN_ABBREVIATION_KEY = "trip_origin"
        const val TRIP_DESTINATION_ABBREVIATION_KEY = "trip_destination"
    }
}