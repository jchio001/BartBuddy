package com.app.jonathanchiou.willimissbart

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment

enum class StationType {
    ORIGIN,
    DESTINATION
}

class TripManager(private val sharedPreferences: SharedPreferences,
                  bundle: Bundle?) {

    interface StationListener {
        fun onTripStationChanged(stationType: StationType, stationAbbreviation: String?)
    }

    var originAbbreviation: String? = null
        private set

    var destinationAbbreviation: String? = null
        private set

    private var stationListener: StationListener? = null

    init {
        bundle?.also {
            originAbbreviation = it.getString(TRIP_ORIGIN_ABBREVIATION_KEY)
            destinationAbbreviation = it.getString(TRIP_DESTINATION_ABBREVIATION_KEY)
        }
    }

    fun setStationListener(stationListener: StationListener) {
        this.stationListener = stationListener
    }

    fun updateStation(fragment: Fragment, stationType: StationType) {
        val intent = Intent(fragment.context, StationSelectionActivity::class.java)
        intent.putExtra(STATION_SELECTION_TYPE_KEY, stationType.toString())
        fragment.startActivityForResult(intent, STATIONS_SELECTION_CODE)
    }

    fun swapTripStations() {
        val placeholder = originAbbreviation
        originAbbreviation = destinationAbbreviation
        destinationAbbreviation = placeholder
        stationListener?.also {
            it.onTripStationChanged(StationType.ORIGIN, originAbbreviation)
            it.onTripStationChanged(StationType.DESTINATION, destinationAbbreviation)
        }
    }

    fun onStationSelectionResult(requestCode: Int,
                                 resultCode: Int,
                                 data: Intent?) {
        if (requestCode == STATIONS_SELECTION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val stationType = StationType.valueOf(data!!.getStringExtra(STATION_SELECTION_TYPE_KEY))
                val station = data.getParcelableExtra<Station>(SELECTED_STATION_KEY)
                stationListener?.onTripStationChanged(stationType, station.abbr)
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