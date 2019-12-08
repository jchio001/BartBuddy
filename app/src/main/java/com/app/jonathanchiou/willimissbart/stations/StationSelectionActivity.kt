package com.app.jonathanchiou.willimissbart.stations

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
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripManager.Companion.STATION_SELECTION_TYPE_KEY
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.view.ViewBindableActivity
import javax.inject.Inject

class StationSelectionActivity : ViewBindableActivity() {

    @Inject lateinit var stationsViewModelFactory: StationsViewModelFactory

    private val progressBar: ProgressBar by bind(R.id.layout_progress_bar)
    private val refreshTextView: TextView by bind(R.id.refresh_textview)
    private val stationsRecyclerView: RecyclerView by bind(R.id.stations_recylerview)

    private val stationsAdapter = StationsAdapter()

    private lateinit var selectionType: String

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
            intent.putExtra(STATION_SELECTION_TYPE_KEY, selectionType)
            intent.putExtra(TripManager.SELECTED_STATION_KEY, station)
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

