package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.MainActivity
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.TripManager.StationListener

class TripsFragment: Fragment() {

    @BindView(R.id.trips_recyclerview)
    lateinit var tripsRecyclerView: RecyclerView

    @BindView(R.id.trips_origin_textview)
    lateinit var tripsOriginTextView: TextView

    @BindView(R.id.trips_destination_textview)
    lateinit var tripsDestinationTextView: TextView

    lateinit var tripManager: TripManager

    private val stationListener = object: StationListener {
        override fun onTripStationChanged(originAbbreviation: String?, destinationAbbreviation: String?) {
            tripsOriginTextView.text = originAbbreviation
            tripsDestinationTextView.text = destinationAbbreviation
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        tripManager = (activity as MainActivity).tripManager
        tripManager.addListener(stationListener)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            tripManager.addListener(stationListener)
        } else {
            tripManager.removeListener(stationListener)
        }
    }
}