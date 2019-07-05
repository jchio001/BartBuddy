package com.app.jonathanchiou.willimissbart.trips

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip

const val REAL_TIME_TRIP = "real_time_trip"

class RealTimeTripInfoActivity: AppCompatActivity() {

    @BindView(R.id.real_time_leg_viewpager)
    lateinit var realTimeLegViewPager: ViewPager

    private lateinit var realTimeTrip: RealTimeTrip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_trip_info)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realTimeTrip = intent.getParcelableExtra(REAL_TIME_TRIP)
        supportActionBar?.title =
            "Trip from ${realTimeTrip.originAbbreviation} to ${realTimeTrip.destinationAbbreviation}"

        realTimeLegViewPager.adapter = RealTimeLegPagerAdapter(realTimeTrip.realTimeLegs)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
