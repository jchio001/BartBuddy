package com.app.jonathan.willimissbart.trips

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTripsParentFragment
import com.app.jonathan.willimissbart.stations.StationSelectionActivity
import javax.inject.Inject
import javax.inject.Singleton

enum class StationType {
    ORIGIN,
    DESTINATION
}

@Singleton
class TripManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    interface EditCallbacks {
        fun onTripUnchanged()
        fun onDuplicateStationSelection(stationType: StationType)
    }

    interface TripStationListener {
        fun onTripStationChanged(originTitle: String?, destinationTitle: String?)
    }

    private var previousOriginAbbreviation: String? = null
    private var previousOriginName: String? = null

    private var previousDestinationAbbreviation: String? = null
    private var previousDestinationName: String? = null

    private var originAbbreviation: String? = null
    private var originName: String? = null

    private var destinationAbbreviation: String? = null
    private var destinationName: String? = null

    var tripEditedListener: TripStationListener? = null
        set(tripEditedListener) {
            field = tripEditedListener
            invokeTripEditedListener()
        }

    var editCallbacks: EditCallbacks? = null

    init {
        sharedPreferences.also {
            previousOriginAbbreviation = it.getString(EXTRA_TRIP_ORIGIN_ABBREVIATION, null)
            previousOriginName = it.getString(EXTRA_TRIP_ORIGIN_NAME, null)

            previousDestinationAbbreviation = it.getString(EXTRA_TRIP_DESTINATION_ABBREVIATION, null)
            previousDestinationName = it.getString(EXTRA_TRIP_DESTINATION_NAME, null)

            originAbbreviation = previousOriginAbbreviation
            originName = previousOriginName
            destinationAbbreviation = previousDestinationAbbreviation
            destinationName = previousDestinationName
        }
    }

    fun getOriginAbbreviation() = previousOriginAbbreviation
    fun getDestinationAbbreviation() = previousDestinationAbbreviation

    fun updateStation(fragment: Fragment, stationType: StationType) {
        val intent = Intent(fragment.context, StationSelectionActivity::class.java)
        intent.putExtra(EXTRA_STATION_SELECTION_TYPE, stationType.toString())
        fragment.startActivityForResult(
            intent,
            STATIONS_SELECTION_CODE)
    }

    fun swapTripStations() {
        val abbreviationPlaceholder = originAbbreviation
        val namePlaceholder = originName

        originAbbreviation = destinationAbbreviation
        originName = destinationName
        destinationAbbreviation = abbreviationPlaceholder
        destinationName = namePlaceholder
        invokeTripEditedListener()
    }

    fun onStationSelectionResult(requestCode: Int,
                                 resultCode: Int,
                                 data: Intent?) {
        if (requestCode == STATIONS_SELECTION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val stationType = StationType.valueOf(data!!.getStringExtra(EXTRA_STATION_SELECTION_TYPE)!!)
                data.getParcelableExtra<Station>(EXTRA_SELECTED_STATION)!!.also {
                    if (stationType == StationType.ORIGIN) {
                        if (it.abbr == destinationAbbreviation) {
                            editCallbacks?.onDuplicateStationSelection(StationType.ORIGIN)
                            return
                        }

                        originAbbreviation = it.abbr
                        originName = it.name
                    } else {
                        if (it.abbr == originAbbreviation) {
                            editCallbacks?.onDuplicateStationSelection(StationType.DESTINATION)
                            return
                        }

                        destinationAbbreviation = it.abbr
                        destinationName = it.name
                    }

                    invokeTripEditedListener()
                }
            }
        }
    }

    fun revertPendingChanges() {
        originAbbreviation = previousOriginAbbreviation
        originName = previousOriginName
        destinationAbbreviation = previousDestinationAbbreviation
        destinationName = previousDestinationName
    }

    fun displayTripsFragment(fragment: Fragment, containerId: Int) {
        if (originAbbreviation != null && destinationAbbreviation != null
            && (previousOriginAbbreviation != originAbbreviation
                || previousDestinationAbbreviation != destinationAbbreviation)
        ) {

            sharedPreferences
                .edit()
                .putString(EXTRA_TRIP_ORIGIN_ABBREVIATION, originAbbreviation)
                .putString(EXTRA_TRIP_ORIGIN_NAME, originName)
                .putString(EXTRA_TRIP_DESTINATION_ABBREVIATION, destinationAbbreviation)
                .putString(EXTRA_TRIP_DESTINATION_NAME, destinationName)
                .apply()

            previousOriginAbbreviation = originAbbreviation
            previousOriginName = originName
            previousDestinationAbbreviation = destinationAbbreviation
            previousDestinationName = destinationName

            fragment.parentFragmentManager.also {
                val tripParentFragment =
                    it.findFragmentByTag(RealTimeTripsParentFragment.BACKSTACK_TAG) ?: RealTimeTripsParentFragment()

                it.popBackStack()
                it.beginTransaction()
                    .replace(containerId, tripParentFragment)
                    .commit()
            }
        }

        if (previousOriginAbbreviation == originAbbreviation
            && previousDestinationAbbreviation == destinationAbbreviation
        ) {
            editCallbacks?.onTripUnchanged()
        }
    }

    private fun invokeTripEditedListener() {
        tripEditedListener?.onTripStationChanged(
            if (originAbbreviation != null) "($originAbbreviation) $originName" else null,
            if (destinationAbbreviation != null) "($destinationAbbreviation) $destinationName" else null)
    }

    companion object {

        const val STATIONS_SELECTION_CODE = 73

        const val EXTRA_STATION_SELECTION_TYPE = "station_selection_type"
        const val EXTRA_SELECTED_STATION = "selected_station"

        const val EXTRA_TRIP_ORIGIN_ABBREVIATION = "trip_origin_abbr"
        const val EXTRA_TRIP_ORIGIN_NAME = "trip_origin_name"

        const val EXTRA_TRIP_DESTINATION_ABBREVIATION = "trip_destination"
        const val EXTRA_TRIP_DESTINATION_NAME = "trip_destination_key"
    }
}
