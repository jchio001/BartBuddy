package com.app.jonathan.willimissbart.trips

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.TripActivity
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.trips.RealTimeTripInfoActivity.Companion.EXTRA_REAL_TIME_TRIP
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathan.willimissbart.utils.models.State
import com.app.jonathan.willimissbart.utils.view.BaseFragment
import javax.inject.Inject

class RealTimeTripFragment : BaseFragment(R.layout.fragment_real_time_trip),
    RealTimeTripsAdapter.Callbacks {

    @Inject lateinit var realTimeTripViewModelFactory: TripViewModelFactory
    @Inject lateinit var tripManager: TripManager

    private val progressBar: ProgressBar by bind(R.id.progress_bar)
    private val recyclerView: RecyclerView by bind(R.id.recycler_view)

    private val isReturnTrip: Boolean by argument(ARG_IS_RETURN_TRIP)

    lateinit var realTimeTripViewModel: RealTimeTripViewModel

    private val realTimeTripAdapter = RealTimeTripsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        recyclerView.adapter = realTimeTripAdapter
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = LinearLayoutManager(context)

        realTimeTripAdapter.callbacks = this

        realTimeTripViewModel = ViewModelProviders
            .of(this, realTimeTripViewModelFactory)
            .get(RealTimeTripViewModel::class.java)
        realTimeTripViewModel.realTimeTripLiveData
            .observe(viewLifecycleOwner, Observer {
                if (it.state == State.PENDING) {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else if (it.state == State.DONE) {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    realTimeTripAdapter.submitList(it.data!!)
                }
            })

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
        intent.putExtra(EXTRA_REAL_TIME_TRIP, realTimeTrip)
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

        const val ARG_IS_RETURN_TRIP = "is_return_trip"

        fun newInstance(isReturnTrip: Boolean) = RealTimeTripFragment().setArguments {
            putBoolean(ARG_IS_RETURN_TRIP, isReturnTrip)
        }
    }
}
