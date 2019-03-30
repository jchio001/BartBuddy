package com.app.jonathanchiou.willimissbart.trips

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.TripManager.StationListener

class TripSelectionFragment: Fragment() {

    @BindView(R.id.origin_station_textview)
    lateinit var originStationTextView: TextView

    @BindView(R.id.destination_station_textview)
    lateinit var destinationStationTextView: TextView

    lateinit var tripManager: TripManager

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_selection, container, false)
    }

    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        tripManager = TripManager(
            PreferenceManager.getDefaultSharedPreferences(context),
            arguments)
        tripManager.setStationListener(object: StationListener {
            override fun onTripStationChanged(stationType: StationType, stationAbbreviation: String?) {
                (if (stationType == StationType.ORIGIN) originStationTextView
                else destinationStationTextView)
                    .text = stationAbbreviation
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tripManager.onStationSelectionResult(requestCode, resultCode, data)
    }

    @OnClick(
        R.id.origin_station_textview,
        R.id.destination_station_textview)
    fun onStationTextViewClicked(view: View) {
        tripManager.updateStation(this,
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
}