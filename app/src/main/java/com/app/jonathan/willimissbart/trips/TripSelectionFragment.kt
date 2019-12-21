package com.app.jonathan.willimissbart.trips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.app.jonathan.willimissbart.MainActivity
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.navigation.fragment.BackStackConsumingFragment
import com.app.jonathan.willimissbart.trips.TripManager.EditCallbacks
import com.app.jonathan.willimissbart.trips.TripManager.TripStationListener
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class TripSelectionFragment : BackStackConsumingFragment() {

    val coordinatorContainer: CoordinatorLayout by bind(R.id.coordinator_container)
    val originStationTextView: TextView by bind(R.id.origin_station_textview)
    val destinationStationTextView: TextView by bind(R.id.destination_station_textview)

    @Inject lateinit var tripManager: TripManager

    private val tripUnchangedListener = object : EditCallbacks {

        override fun onTripUnchanged() {
            coordinatorContainer.showSnackbar(R.string.stations_unchanged_error)
        }

        override fun onDuplicateStationSelection(stationType: StationType) {
            coordinatorContainer.showSnackbar(R.string.stations_duplicate_error)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_trip_selection, container, false)

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        bindClick(
            R.id.origin_station_textview,
            R.id.destination_station_textview) { stationTextView ->
            tripManager.updateStation(
                this,
                if (stationTextView.id == R.id.origin_station_textview) StationType.ORIGIN
                else StationType.DESTINATION)
        }
        bindClick(R.id.swap_icon) { tripManager.swapTripStations() }
        bindClick(R.id.submit_trip_button) { tripManager.displayTripsFragment(this, R.id.parent) }

        tripManager = (activity as MainActivity).tripManager
        tripManager.tripEditedListener = object : TripStationListener {

            override fun onTripStationChanged(originTitle: String?, destinationTitle: String?) {
                originStationTextView.text = originTitle
                destinationStationTextView.text = destinationTitle
            }
        }
        tripManager.editCallbacks = tripUnchangedListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tripManager.onStationSelectionResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tripManager.tripEditedListener = null
    }

    override fun onBackPressed(): Boolean {
        tripManager.revertPendingChanges()
        return false
    }

    fun View.showSnackbar(@StringRes messageResource: Int) {
        Snackbar
            .make(
                this,
                messageResource,
                Snackbar.LENGTH_SHORT)
            .setActionTextColor(ContextCompat.getColor(context!!, android.R.color.white))
            .also {
                it.view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            }
            .show()
    }

    companion object {

        const val BACKSTACK_TAG = "trip_selection_fragment"
    }
}
