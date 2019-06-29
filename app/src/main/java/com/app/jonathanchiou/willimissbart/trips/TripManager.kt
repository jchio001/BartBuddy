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

    interface TripUnchangedListener {
        fun onTripUnchanged()
    }

    interface TripStationListener {
        fun onTripStationChanged(originTitle: String?, destinationTitle: String?)
    }

    private var previousOriginAbbreviation: String? = null
    private var previousOriginName: String? = null

    private var previousDestinationAbbreviation: String? = null
    private var previousDestinationName: String? = null

    var originAbbreviation: String? = null
        private set
    private var originName: String? = null

    var destinationAbbreviation: String? = null
        private set
    private var destinationName: String? = null

    var tripEditedListener: TripStationListener? = null
    set(tripEditedListener) {
       field = tripEditedListener
        invokeTripEditedListener()
    }

    var tripUnchangedListener: TripUnchangedListener? = null

    init {
        sharedPreferences.also {
            previousOriginAbbreviation =
                it.getString(TRIP_ORIGIN_ABBREVIATION_KEY, null)
            previousOriginName =
                it.getString(TRIP_ORIGIN_NAME_KEY, null)

            previousDestinationAbbreviation =
                it.getString(TRIP_DESTINATION_ABBREVIATION_KEY, null)
            previousDestinationName =
                it.getString(TRIP_DESTINATION_NAME_KEY, null)

            originAbbreviation = previousOriginAbbreviation
            originName = previousOriginName
            destinationAbbreviation = previousDestinationAbbreviation
            destinationName = previousDestinationName
        }
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
        invokeTripEditedListener()
    }

    fun onStationSelectionResult(requestCode: Int,
                                 resultCode: Int,
                                 data: Intent?) {
        if (requestCode == STATIONS_SELECTION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val stationType = StationType.valueOf(data!!.getStringExtra(
                    STATION_SELECTION_TYPE_KEY))

                 data.getParcelableExtra<Station>(SELECTED_STATION_KEY).also {
                     if (stationType == StationType.ORIGIN) {
                         originAbbreviation = it.abbr
                         originName = it.name
                     } else {
                         destinationAbbreviation = it.abbr
                         destinationName = it.name
                     }

                     invokeTripEditedListener()
                 }
            }
        }
    }

    fun displayTripsFragment(fragment: Fragment, containerId: Int) {
        if (originAbbreviation != null && destinationAbbreviation != null
            && (previousOriginAbbreviation != originAbbreviation
                || previousDestinationAbbreviation != previousDestinationAbbreviation)) {

            sharedPreferences
                .edit()
                .putString(TRIP_ORIGIN_ABBREVIATION_KEY, originAbbreviation)
                .putString(TRIP_ORIGIN_NAME_KEY, originName)
                .putString(TRIP_DESTINATION_ABBREVIATION_KEY, destinationAbbreviation)
                .putString(TRIP_DESTINATION_NAME_KEY, destinationName)
                .apply()

            previousOriginAbbreviation = originAbbreviation
            previousDestinationAbbreviation = destinationAbbreviation

            fragment.fragmentManager!!.also {
                val tripParentFragment = it.findFragmentByTag(TripParentFragment.BACKSTACK_TAG) ?: TripParentFragment()

                it.popBackStack()
                it.beginTransaction()
                    .replace(containerId, tripParentFragment)
                    .commit()
            }
        }

        if (previousOriginAbbreviation == originAbbreviation
            && previousDestinationAbbreviation == destinationAbbreviation) {
            tripUnchangedListener?.onTripUnchanged()
        }
    }

    private fun invokeTripEditedListener() {
        tripEditedListener?.onTripStationChanged(
            if (originAbbreviation != null) "($originAbbreviation) $originName" else null,
            if (destinationAbbreviation != null) "($destinationAbbreviation) $destinationName" else null)
    }

    companion object {
        const val STATIONS_SELECTION_CODE = 73

        const val STATION_SELECTION_TYPE_KEY = "station_selection_type"
        const val SELECTED_STATION_KEY = "selected_station"

        const val TRIP_ORIGIN_ABBREVIATION_KEY = "trip_origin_abbr"
        const val TRIP_ORIGIN_NAME_KEY = "trip_origin_name"

        const val TRIP_DESTINATION_ABBREVIATION_KEY = "trip_destination"
        const val TRIP_DESTINATION_NAME_KEY = "trip_destination_key"
    }
}