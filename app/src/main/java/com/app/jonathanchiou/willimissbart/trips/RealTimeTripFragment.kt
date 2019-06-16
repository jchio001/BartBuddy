package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.MainActivity
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.api.ApiClient
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.trips.TripManager.StationListener
import com.app.jonathanchiou.willimissbart.utils.models.State

class RealTimeTripViewModelFactory(private val bartService: BartService):
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == RealTimeTripViewModel::class.java) {
            return RealTimeTripViewModel(bartService) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}

class RealTimeTripFragment: Fragment() {

    @BindView(R.id.container)
    lateinit var container: FrameLayout

    @BindView(R.id.trips_origin_textview)
    lateinit var tripsOriginTextView: TextView

    @BindView(R.id.trips_destination_textview)
    lateinit var tripsDestinationTextView: TextView

    lateinit var tripManager: TripManager

    lateinit var realTimeTripViewModel: RealTimeTripViewModel

    val realTimeTripAdapter = RealTimeTripAdapter()

    private val stationListener = object: StationListener {
        override fun onTripStationChanged(originAbbreviation: String?, destinationAbbreviation: String?) {
            tripsOriginTextView.text = originAbbreviation
            tripsDestinationTextView.text = destinationAbbreviation
            realTimeTripViewModel.requestTrip(originAbbreviation!!, destinationAbbreviation!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_real_time_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        realTimeTripViewModel = ViewModelProviders
            .of(this,
                RealTimeTripViewModelFactory(
                    ApiClient.INSTANCE
                        .bartService))
            .get(RealTimeTripViewModel::class.java)
        realTimeTripViewModel.realTimeTripLiveData
            .observe(viewLifecycleOwner, Observer {
                if (it.state == State.DONE) {
                    if (container.childCount == 0
                        || container.getChildAt(0).id != R.id.recyclerview) {
                        val recyclerView = LayoutInflater.from(context)
                            .inflate(R.layout.layout_model_recyclerview, container, false)
                            as RecyclerView
                        recyclerView.adapter = realTimeTripAdapter
                        recyclerView.layoutManager = LinearLayoutManager(context)

                        container.removeAllViews()
                        container.addView(recyclerView)
                    }

                    realTimeTripAdapter.setTrips(it.data!!)
                }
            })

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