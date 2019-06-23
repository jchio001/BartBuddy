package com.app.jonathanchiou.willimissbart.trips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.app.jonathanchiou.willimissbart.MainActivity
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.navigation.fragment.BackStackConsumingFragment
import com.app.jonathanchiou.willimissbart.trips.TripManager.StationListener
import com.app.jonathanchiou.willimissbart.trips.TripManager.TripUnchangedListener
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(color: Int) {
    Snackbar
        .make(
            this,
            R.string.stations_unchanged_error,
            Snackbar.LENGTH_SHORT)
        .setActionTextColor(ContextCompat.getColor(context!!, android.R.color.white))
        .also {
            it.view.setBackgroundColor(ContextCompat.getColor(context!!, color))
        }
        .show()
}

class TripSelectionFragment: BackStackConsumingFragment() {

    @BindView(R.id.coordinator_container)
    lateinit var coordinatorContainer: CoordinatorLayout

    @BindView(R.id.origin_station_textview)
    lateinit var originStationTextView: TextView

    @BindView(R.id.destination_station_textview)
    lateinit var destinationStationTextView: TextView

    lateinit var tripManager: TripManager

    private val stationListener = object: StationListener {

        override fun onTripStationChanged(originAbbreviation: String?, destinationAbbreviation: String?) {
            originStationTextView.text = originAbbreviation
            destinationStationTextView.text = destinationAbbreviation
        }
    }

    private val tripUnchangedListener = object: TripUnchangedListener {

        override fun onTripUnchanged() {
            coordinatorContainer.showSnackbar(R.color.colorPrimary)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_selection, container, false)
    }

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        tripManager = (activity as MainActivity).tripManager
        tripManager.addListener(stationListener)
        tripManager.tripUnchangedListener = tripUnchangedListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tripManager.onStationSelectionResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tripManager.removeListener(stationListener)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    @OnClick(
        R.id.origin_station_textview,
        R.id.destination_station_textview)
    fun onStationTextViewClicked(view: View) {
        tripManager.updateStation(
            this,
            if (view.id == R.id.origin_station_textview) StationType.ORIGIN
            else StationType.DESTINATION)
    }

    @OnClick(R.id.swap_icon)
    fun onSwapIconClicked() {
        tripManager.swapTripStations()
    }

    @OnClick(R.id.submit_trip_button)
    fun submitTripButton() {
        tripManager.displayTripsFragment(this, R.id.parent)
    }

    companion object {

        const val BACKSTACK_TAG = "trip_selection_fragment"
    }
}