package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import io.reactivex.functions.BiConsumer
import java.lang.IllegalStateException
import javax.inject.Inject

const val REAL_TIME_TRIP = "real_time_trip"

class RealTimeTripInfoActivity: AppCompatActivity() {

    @BindView(R.id.real_time_leg_recyclerview)
    lateinit var realTimeLegRecyclerView: RecyclerView

    @Inject
    lateinit var tripViewModelFactory: TripViewModelFactory

    private lateinit var realTimeTrip: RealTimeTrip

    lateinit var realTimeLegViewModel: RealTimeLegViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_trip_info)
        ButterKnife.bind(this)
        appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        realTimeLegViewModel = ViewModelProviders.of(this, tripViewModelFactory)
            .get(RealTimeLegViewModel::class.java)

        realTimeTrip = intent.getParcelableExtra(REAL_TIME_TRIP)
        supportActionBar?.title =
            "Trip from ${realTimeTrip.originAbbreviation} to ${realTimeTrip.destinationAbbreviation}"

        realTimeLegRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false)
        val realTimeLegPagerAdapter = RealTimeLegPagerAdapter(realTimeTrip.realTimeLegs)
        realTimeLegRecyclerView.adapter = realTimeLegPagerAdapter

        realTimeLegPagerAdapter.onRequestRealTimeLeg = BiConsumer(realTimeLegViewModel::requestRealTimeLeg)

        realTimeLegViewModel.realTimeLegLiveData
            .observe(this, Observer {
                if (it.state == State.DONE) {
                    realTimeLegPagerAdapter.updateItem(it.data!!, it.query!!)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
