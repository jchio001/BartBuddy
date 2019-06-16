package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip

class TripViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.departure_time_textview)
    lateinit var departureTimeTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }
}

class RealTimeTripAdapter: RecyclerView.Adapter<TripViewHolder>() {

    private var trips: List<Trip> = ArrayList()
    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.cell_real_time_trip, parent, false))
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.let {
            it.departureTimeTextView.text = trips[position].originDepartureTime
        }
    }
}