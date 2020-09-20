package com.app.jonathan.willimissbart.stations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.trips.TripManager
import com.app.jonathan.willimissbart.trips.TripManager.Companion.EXTRA_STATION_SELECTION_TYPE
import com.app.jonathan.willimissbart.utils.view.BaseActivity
import com.app.jonathan.willimissbart.utils.view.isVisible
import javax.inject.Inject

class StationSelectionActivity : BaseActivity(R.layout.activity_station_selection) {

    @Inject lateinit var stationsViewModelFactory: StationSelectionViewModelFactory

    private val progressBar: ProgressBar by bind(R.id.layout_progress_bar)
    private val errorTextView: TextView by bind(R.id.error_textview)
    private val stationsRecyclerView: RecyclerView by bind(R.id.stations_recylerview)

    private val selectionType: String by extra(EXTRA_STATION_SELECTION_TYPE)

    private val stationsAdapter = StationsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val stationsViewModel = ViewModelProviders.of(this, stationsViewModelFactory)
            .get(StationSelectionViewModel::class.java)
        stationsViewModel
            .stationsLiveData
            .observe(this) { stationsViewState ->
                progressBar.isVisible = stationsViewState.showProgressBar
                errorTextView.isVisible = stationsViewState.showErrorTextView
                stationsRecyclerView.isVisible = stationsViewState.showStationsRecyclerView

                if (stationsViewState.showStationsRecyclerView) {
                    stationsAdapter.submitList(stationsViewState.stations!!)
                }
            }

        stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        stationsRecyclerView.adapter = stationsAdapter

        stationsAdapter.onClickListener = Consumer { station ->
            val intent = Intent()
            intent.putExtra(EXTRA_STATION_SELECTION_TYPE, selectionType)
            intent.putExtra(TripManager.EXTRA_SELECTED_STATION, station)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        stationsViewModel.getStations()
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

