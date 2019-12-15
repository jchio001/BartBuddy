package com.app.jonathanchiou.willimissbart.trips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.MainActivity
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripInfoActivity.Companion.REAL_TIME_TRIP
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.view.ViewBindableFragment
import javax.inject.Inject

fun createRealTimeTripFragment(isReturnTrip: Boolean): RealTimeTripFragment {
    val realTimeTripFragment = RealTimeTripFragment()

    if (isReturnTrip) {
        realTimeTripFragment.arguments = Bundle().also {
            it.putBoolean(RealTimeTripFragment.IS_RETURN_TRIP_KEY, true)
        }
    }

    return realTimeTripFragment
}

class RealTimeTripFragment : ViewBindableFragment(), RealTimeTripsAdapter.Callbacks {

    val container: FrameLayout by bind(R.id.container)

    @Inject
    lateinit var realTimeTripViewModelFactory: TripViewModelFactory

    lateinit var tripManager: TripManager

    lateinit var realTimeTripViewModel: RealTimeTripViewModel

    private val realTimeTripAdapter = RealTimeTripsAdapter()

    private var isReturnTrip = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_real_time_trips, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        arguments?.also {
            isReturnTrip = it.getBoolean(IS_RETURN_TRIP_KEY, false)
        }

        realTimeTripAdapter.callbacks = this

        realTimeTripViewModel = ViewModelProviders
            .of(this, realTimeTripViewModelFactory)
            .get(RealTimeTripViewModel::class.java)
        realTimeTripViewModel.realTimeTripLiveData
            .observe(viewLifecycleOwner, Observer {
                if (it.state == State.PENDING) {
                    if (container.childCount == 0
                        || container.getChildAt(0).id != R.id.layout_progress_bar
                    ) {
                        val progressBar = LayoutInflater.from(context)
                            .inflate(R.layout.layout_progress_bar, container, false)

                        container.removeAllViews()
                        container.addView(progressBar)
                    }
                } else if (it.state == State.DONE) {
                    if (container.childCount == 0
                        || container.getChildAt(0).id != R.id.recyclerview
                    ) {
                        val recyclerView = LayoutInflater.from(context)
                            .inflate(R.layout.layout_model_recyclerview, container, false)
                            as RecyclerView
                        recyclerView.adapter = realTimeTripAdapter
                        recyclerView.itemAnimator = null
                        recyclerView.layoutManager = LinearLayoutManager(context)

                        container.removeAllViews()
                        container.addView(recyclerView)
                    }

                    realTimeTripAdapter.submitList(it.data!!)
                }
            })

        tripManager = (activity as MainActivity).tripManager
        requestTrip(tripManager.getOriginAbbreviation()!!, tripManager.getDestinationAbbreviation()!!)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            requestTrip(tripManager.getOriginAbbreviation()!!, tripManager.getDestinationAbbreviation()!!)
        }
    }

    override fun onRealTimeTripClicked(realTimeTrip: RealTimeTrip) {
        val intent = Intent(context, RealTimeTripInfoActivity::class.java)
        intent.putExtra(REAL_TIME_TRIP, realTimeTrip)
        startActivity(intent)
    }

    private fun requestTrip(
        originAbbreviation: String,
        destinationAbbreviation: String
    ) {
        val actualOriginAbbreviation = if (!isReturnTrip) originAbbreviation else destinationAbbreviation
        val actualDestinationAbbreviation = if (!isReturnTrip) destinationAbbreviation else originAbbreviation

        (parentFragment as RealTimeTripsParentFragment?)?.also {
            it.title.text = "$actualOriginAbbreviation to $actualDestinationAbbreviation"
        }

        realTimeTripViewModel.requestTrip(actualOriginAbbreviation, actualDestinationAbbreviation)
    }

    companion object {

        const val IS_RETURN_TRIP_KEY = "is_return_trip"
    }
}
