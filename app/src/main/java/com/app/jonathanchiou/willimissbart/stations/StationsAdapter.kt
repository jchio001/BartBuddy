package com.app.jonathanchiou.willimissbart.stations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.stations.models.api.Station
import com.app.jonathanchiou.willimissbart.utils.BasicDiffCallback

class StationViewHolder(itemView: View) : ViewHolder(itemView) {

    val stationTextView: TextView = itemView.findViewById(R.id.station_name_textview)
    val stationLocationTextView: TextView = itemView.findViewById(R.id.station_location_textview)

    fun bind(station: Station) {
        stationTextView.text = station.name
        stationLocationTextView.text = "${station.city}, ${station.state}"
    }
}

class StationsAdapter : ListAdapter<Station, StationViewHolder>(BasicDiffCallback<Station>()) {

    private lateinit var recyclerView: RecyclerView

    private var isBeingClicked = false
    var onClickListener: Consumer<Station>? = null
    private val debouncedOnClickListener = View.OnClickListener {
        if (!isBeingClicked) {
            isBeingClicked = true
            onClickListener?.accept(getItem(recyclerView.getChildAdapterPosition(it)))
            isBeingClicked = false
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_station_view, parent, false)
        view.setOnClickListener(debouncedOnClickListener)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) = holder.bind(getItem(position))
}
