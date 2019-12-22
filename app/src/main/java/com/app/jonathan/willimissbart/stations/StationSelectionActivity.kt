package com.app.jonathan.willimissbart.stations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.trips.TripManager
import com.app.jonathan.willimissbart.trips.TripManager.Companion.EXTRA_STATION_SELECTION_TYPE
import com.app.jonathan.willimissbart.utils.models.State
import com.app.jonathan.willimissbart.utils.view.BaseActivity
import javax.inject.Inject

class StationSelectionActivity : BaseActivity(R.layout.activity_station_selection) {

    @Inject lateinit var stationsViewModelFactory: StationsViewModelFactory

    private val progressBar: ProgressBar by bind(R.id.layout_progress_bar)
    private val refreshTextView: TextView by bind(R.id.refresh_textview)
    private val stationsRecyclerView: RecyclerView by bind(R.id.stations_recylerview)

    private val selectionType: String by extra(EXTRA_STATION_SELECTION_TYPE)

    private val stationsAdapter = StationsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val stationsViewModel = ViewModelProviders.of(this, stationsViewModelFactory)
            .get(StationsViewModel::class.java)
        stationsViewModel
            .getStationsLiveData()
            .observe(this, Observer {
                when (it.state) {
                    State.PENDING -> {
                        progressBar.visibility = View.VISIBLE
                        refreshTextView.visibility = View.GONE
                        stationsRecyclerView.visibility = View.GONE
                    }
                    State.DONE -> {
                        progressBar.visibility = View.GONE
                        refreshTextView.visibility = View.GONE
                        stationsRecyclerView.visibility = View.VISIBLE
                        stationsAdapter.submitList(it.data!!)
                    }
                    State.ERROR -> {
                        progressBar.visibility = View.GONE
                        refreshTextView.visibility = View.VISIBLE
                        stationsRecyclerView.visibility = View.GONE
                    }
                }
            })

        bindClick(R.id.refresh_textview) { stationsViewModel.requestStations() }

        stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        stationsRecyclerView.adapter = stationsAdapter

        stationsAdapter.onClickListener = Consumer { station ->
            val intent = Intent()
            intent.putExtra(EXTRA_STATION_SELECTION_TYPE, selectionType)
            intent.putExtra(TripManager.EXTRA_SELECTED_STATION, station)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

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

