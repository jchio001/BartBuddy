package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip

class TripViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.destination_textview)
    lateinit var departureTimeTextView: TextView

    @BindView(R.id.time_until_arrival_textview)
    lateinit var timeUntilArrivalTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }
}

class RealTimeTripAdapter: RecyclerView.Adapter<TripViewHolder>() {

    private var realTimeTrips: List<RealTimeTrip> = ArrayList()
    fun setTrips(realTimeTrips: List<RealTimeTrip>) {
        this.realTimeTrips = realTimeTrips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.cell_real_time_trip, parent, false))
    }

    override fun getItemCount(): Int {
        return realTimeTrips.size
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        realTimeTrips[position].originEtds[0].let {
            holder.departureTimeTextView.text = "To ${it.destination}"
            holder.timeUntilArrivalTextView.text = "${it.estimates[0].minutes} min"
        }
    }
}