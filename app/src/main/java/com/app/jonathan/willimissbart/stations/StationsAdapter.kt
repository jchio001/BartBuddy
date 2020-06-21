package com.app.jonathan.willimissbart.stations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.stations.models.api.ApiStation
import com.app.jonathan.willimissbart.utils.BasicDiffCallback
import com.app.jonathan.willimissbart.utils.view.DebouncingOnClickListener

class StationViewHolder(itemView: View) : ViewHolder(itemView) {

    val stationTextView: TextView = itemView.findViewById(R.id.station_name_textview)
    val stationLocationTextView: TextView = itemView.findViewById(R.id.station_location_textview)

    fun bind(apiStation: ApiStation) {
        stationTextView.text = "(${apiStation.abbr}) ${apiStation.name}"
        stationLocationTextView.text = "${apiStation.city}, ${apiStation.state}"
    }
}

class StationsAdapter : ListAdapter<ApiStation, StationViewHolder>(BasicDiffCallback<ApiStation>()) {

    private lateinit var recyclerView: RecyclerView

    var onClickListener: Consumer<ApiStation>? = null
    private val debouncedOnClickListener = DebouncingOnClickListener { view ->
        onClickListener?.accept(getItem(recyclerView.getChildAdapterPosition(view)))
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
