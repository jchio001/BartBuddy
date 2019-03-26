package com.app.jonathanchiou.willimissbart

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.util.Consumer
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

class StationSelectionActivity : AppCompatActivity() {

    @BindView(R.id.container)
    lateinit var container: FrameLayout

    lateinit var selectionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_selection)
        ButterKnife.bind(this)

        selectionType = intent!!.getStringExtra(STATION_SELECTION_TYPE)!!

        val stationsViewModel = ViewModelProviders.of(this)
            .get(StationsViewModel::class.java)
        stationsViewModel
            .stationsLiveData
            .observe(this, Observer {
                when (it.state) {
                    State.PENDING -> {
                        if (container.childCount == 0 || container.getChildAt(0) !is ProgressBar) {
                            container.removeAllViews()
                            container.addView(
                                LayoutInflater.from(this)
                                    .inflate(R.layout.layout_progress_bar,
                                             container,
                                             false))
                        }
                    }
                    State.DONE -> {
                        if (container.childCount == 0 || container.getChildAt(0) !is StationsRecyclerView) {
                            container.removeAllViews()

                            val stationsRecyclerView = LayoutInflater.from(this)
                                .inflate(R.layout.layout_stations_recyclerview,
                                         container,
                                         false) as StationsRecyclerView
                            stationsRecyclerView.selectionType = selectionType
                            stationsRecyclerView.stationsAdapter.setStations(it.data!!)

                            container.addView(stationsRecyclerView)
                        }
                    }
                }
            })
        stationsViewModel.requestStations()
    }

    companion object {
        const val STATION_SELECTION_TYPE = "station_selection_type"

        const val ORIGIN_SELECTION = "origin"
        const val DESTINATION_SELECTION = "destination"

        const val SELECTED_STATION_KEY = "selected_station"
    }
}
