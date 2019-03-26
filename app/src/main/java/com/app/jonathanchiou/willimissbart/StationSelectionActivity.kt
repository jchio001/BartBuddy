package com.app.jonathanchiou.willimissbart

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.util.Consumer
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

class StationSelectionActivity : AppCompatActivity() {

    @BindView(R.id.stations_recylerview)
    lateinit var stationsRecyclerView: RecyclerView

    lateinit var stationsAdapter: StationsAdapter

    lateinit var selectionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_selection)
        ButterKnife.bind(this)

        selectionType = intent!!.getStringExtra(STATION_SELECTION_TYPE)!!

        stationsAdapter = StationsAdapter(stationsRecyclerView)
        stationsAdapter.onClickListener = Consumer {
            val intent = Intent()
            intent.putExtra(STATION_SELECTION_TYPE, selectionType)
            intent.putExtra(SELECTED_STATION_KEY, it)

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        stationsRecyclerView.adapter = stationsAdapter

        val stationsViewModel = ViewModelProviders.of(this)
            .get(StationsViewModel::class.java)
        stationsViewModel
            .stationsLiveData
            .observe(this, Observer {
                if (it.state == State.DONE) {
                    stationsAdapter.setStations(it.data!!)
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
