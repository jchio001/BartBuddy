package com.app.jonathanchiou.willimissbart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.TripManager.Companion.STATION_SELECTION_TYPE_KEY

class StationSelectionActivity : AppCompatActivity() {

    @BindView(R.id.container)
    lateinit var container: FrameLayout

    lateinit var selectionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_selection)
        ButterKnife.bind(this)

        selectionType = intent!!.getStringExtra(STATION_SELECTION_TYPE_KEY)!!

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
}
