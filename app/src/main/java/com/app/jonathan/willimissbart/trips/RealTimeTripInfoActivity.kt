package com.app.jonathan.willimissbart.trips

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.timer.TimerService.Companion.startRealTimeTripTimer
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathan.willimissbart.utils.view.ViewBindableActivity
import javax.inject.Inject

class RealTimeTripInfoActivity : ViewBindableActivity() {

    val realTimeLegRecyclerView: RecyclerView by bind(R.id.real_time_leg_recyclerview)
    val startTripButton: AppCompatButton by bind(R.id.start_trip_button)

    @Inject lateinit var tripViewModelFactory: TripViewModelFactory

    private lateinit var realTimeTrip: RealTimeTrip

    private lateinit var realTimeLegsCountdownViewModel: RealTimeLegsCountdownViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_trip_info)
        appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realTimeTrip = intent.getParcelableExtra(REAL_TIME_TRIP)!!
        val title = "Trip from ${realTimeTrip.originAbbreviation} to ${realTimeTrip.destinationAbbreviation}"
        supportActionBar?.title = title

        bindClick(R.id.start_trip_button) {
            startRealTimeTripTimer(
                realTimeTrip.copy(
                    realTimeLegs = realTimeLegsCountdownViewModel.realTimeLegsLiveData.value!!
                )
            )
        }

        realTimeLegRecyclerView.itemAnimator = null
        realTimeLegRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false)
        val realTimeLegPagerAdapter = RealTimeLegAdapter()
        realTimeLegRecyclerView.adapter = realTimeLegPagerAdapter

        realTimeLegsCountdownViewModel = ViewModelProviders.of(this)
            .get(RealTimeLegsCountdownViewModel::class.java)
        realTimeLegsCountdownViewModel.realTimeLegsLiveData
            .observe(this, Observer { realTimeLegs ->
                realTimeLegPagerAdapter.submitList(realTimeLegs)
                if (realTimeLegs.first().duration < 0) {
                    startTripButton.isEnabled = false
                }
            })
        realTimeLegsCountdownViewModel.seed(realTimeTrip)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val REAL_TIME_TRIP = "real_time_trip"
    }
}
