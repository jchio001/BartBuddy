package com.app.jonathanchiou.willimissbart.stations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.stations.models.api.Station
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind

class StationViewHolder(itemView: View) : ViewHolder(itemView) {

    val stationTextView: TextView by bind(R.id.station_name_textview)
    val stationLocationTextView: TextView by bind(R.id.station_location_textview)

    fun renderStation(station: Station) {
        stationTextView.text = station.name
        stationLocationTextView.text = "${station.city}, ${station.state}"
    }
}

class StationsAdapter(private val recyclerView: RecyclerView) : Adapter<StationViewHolder>() {

    private var stations: ArrayList<Station> = ArrayList(0)

    private var isBeingClicked = false
    var onClickListener: Consumer<Station>? = null
    private val debouncedOnClickListener = View.OnClickListener {
        if (!isBeingClicked) {
            isBeingClicked = true
            onClickListener?.accept(stations[recyclerView.getChildAdapterPosition(it)])
            isBeingClicked = false
        }
    }

    override fun getItemCount(): Int {
        return stations.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_station_view, parent, false)
        view.setOnClickListener(debouncedOnClickListener)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.renderStation(stations[position])
    }

    fun getStation(index: Int): Station {
        return stations[index]
    }

    fun setStations(stations: List<Station>) {
        this.stations = ArrayList(stations)
        notifyDataSetChanged()
    }
}