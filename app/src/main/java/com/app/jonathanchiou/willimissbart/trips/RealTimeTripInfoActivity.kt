package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.timer.TimerService.Companion.startRealTimeTripTimer
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.viewbinding.ViewBindableActivity
import javax.inject.Inject

class RealTimeTripInfoActivity : ViewBindableActivity() {

    val realTimeLegRecyclerView: RecyclerView by bind(R.id.real_time_leg_recyclerview)

    @Inject lateinit var tripViewModelFactory: TripViewModelFactory

    private lateinit var realTimeTrip: RealTimeTrip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_trip_info)
        appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realTimeTrip = intent.getParcelableExtra(REAL_TIME_TRIP)
        val title = "Trip from ${realTimeTrip.originAbbreviation} to ${realTimeTrip.destinationAbbreviation}"
        supportActionBar?.title = title

        bindClick(R.id.start_trip_button) { startRealTimeTripTimer(realTimeTrip) }

        realTimeLegRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false)
        val realTimeLegPagerAdapter = RealTimeLegPagerAdapter()
        realTimeLegRecyclerView.adapter = realTimeLegPagerAdapter
        realTimeLegPagerAdapter.submitList(realTimeTrip.realTimeLegs)
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
