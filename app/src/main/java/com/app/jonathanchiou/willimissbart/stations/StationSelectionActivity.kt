package com.app.jonathanchiou.willimissbart.stations

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.trips.TripManager.Companion.STATION_SELECTION_TYPE_KEY
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.viewbinding.ViewBindableActivity
import javax.inject.Inject

class StationSelectionActivity : ViewBindableActivity() {

    val container: FrameLayout by bind(R.id.container)

    @Inject
    lateinit var stationsViewModelFactory: StationsViewModelFactory

    lateinit var selectionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_selection)
        appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        selectionType = intent!!.getStringExtra(STATION_SELECTION_TYPE_KEY)!!

        val stationsViewModel = ViewModelProviders.of(this, stationsViewModelFactory)
            .get(StationsViewModel::class.java)
        stationsViewModel
            .getStationsLiveData()
            .observe(this, Observer {
                when (it.state) {
                    State.PENDING -> {
                        if (container.childCount == 0 || container.getChildAt(0) !is ProgressBar) {
                            container.removeAllViews()
                            container.addView(
                                LayoutInflater.from(this)
                                    .inflate(
                                        R.layout.layout_progress_bar,
                                        container,
                                        false))
                        }
                    }
                    State.DONE -> {
                        if (container.childCount == 0 || container.getChildAt(0) !is StationsRecyclerView) {
                            container.removeAllViews()

                            val stationsRecyclerView = LayoutInflater.from(this)
                                .inflate(
                                    R.layout.layout_stations_recyclerview,
                                    container,
                                    false) as StationsRecyclerView
                            stationsRecyclerView.selectionType = selectionType
                            stationsRecyclerView.stationsAdapter.setStations(it.data!!)

                            container.addView(stationsRecyclerView)
                        }
                    }
                    State.ERROR -> {
                        if (container.childCount == 0
                            || container.getChildAt(0).id != R.id.layout_stations_failed
                        ) {
                            container.removeAllViews()

                            val failedStationsLayout = LayoutInflater.from(this)
                                .inflate(
                                    R.layout.layout_stations_failed,
                                    container,
                                    false)
                            failedStationsLayout.findViewById<TextView>(R.id.refresh_textview)
                                .setOnClickListener {
                                    stationsViewModel.requestStations()
                                }

                            container.addView(failedStationsLayout)
                        }
                    }
                }
            })
        stationsViewModel.requestStations()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}

