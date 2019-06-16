package com.app.jonathanchiou.willimissbart.trips

import android.graphics.Color
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

    @BindView(R.id.train_color_indicator)
    lateinit var trainColorIndicator: View

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
        realTimeTrips[position].originEtds[0].also {etd ->
            holder.departureTimeTextView.text = "To ${etd.destination}"
            etd.estimates[0].also {
                holder.timeUntilArrivalTextView.text =
                    if (it.minutes != 0) "${it.minutes} min" else "Leaving..."
                holder.trainColorIndicator
                    .setBackgroundColor(
                        Color.parseColor(it.hexColor))
            }
        }
    }
}