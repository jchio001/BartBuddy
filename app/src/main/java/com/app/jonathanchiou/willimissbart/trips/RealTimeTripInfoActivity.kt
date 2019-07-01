package com.app.jonathanchiou.willimissbart.trips

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip

const val REAL_TIME_TRIP = "real_time_trip"

class RealTimeTripInfoActivity : AppCompatActivity() {

    private lateinit var realTimeTrip: RealTimeTrip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_trip_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realTimeTrip = intent.getParcelableExtra(REAL_TIME_TRIP)
        supportActionBar?.title =
            "Trip from ${realTimeTrip.originAbbreviation} to ${realTimeTrip.destinationAbbreviation}"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
