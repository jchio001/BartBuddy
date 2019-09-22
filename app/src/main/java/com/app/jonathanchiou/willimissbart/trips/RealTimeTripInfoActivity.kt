package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.notification.TimerService.Companion.startRealTimeTripTimer
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bindClick
import javax.inject.Inject

class RealTimeTripInfoActivity : AppCompatActivity() {

    val realTimeLegRecyclerView: RecyclerView by bind(R.id.real_time_leg_recyclerview)
    val startTripButton: CardView by bind(R.id.start_trip_button)

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

        bindClick(R.id.start_trip_button) {
            startRealTimeTripTimer((realTimeTrip.completeRealTimeLegs.first()).estimate.minutes.toLong())
        }

        realTimeLegRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false)
        val realTimeLegPagerAdapter = RealTimeLegPagerAdapter()
        realTimeLegRecyclerView.adapter = realTimeLegPagerAdapter
        realTimeLegPagerAdapter.submitList(realTimeTrip.completeRealTimeLegs)
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
