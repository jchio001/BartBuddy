package com.app.jonathanchiou.willimissbart.trips

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Consumer
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

    var onClickListener: Consumer<RealTimeTrip>? = null

    private var isClicked = false

    private lateinit var recyclerView: RecyclerView

    private val debouncedOnClickListener = View.OnClickListener {
        if (!isClicked) {
            isClicked = true
            onClickListener?.accept(
                realTimeTrips[recyclerView.getChildLayoutPosition(it)])
            isClicked = false
        }
    }

    private var realTimeTrips: List<RealTimeTrip> = ArrayList()
    fun setTrips(realTimeTrips: List<RealTimeTrip>) {
        this.realTimeTrips = realTimeTrips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_real_time_trip, parent, false)
        view.setOnClickListener(debouncedOnClickListener)
        return TripViewHolder(view)
    }

    override fun getItemCount(): Int {
        return realTimeTrips.size
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        realTimeTrips[position].realTimeLegs[0].also { realTimeLeg ->
            holder.departureTimeTextView.text = "To ${realTimeLeg.trainHeadStation}"
            realTimeLeg.estimate.also {
                holder.timeUntilArrivalTextView.text =
                    if (it!!.minutes != 0) "${it!!.minutes} min"
                    else "Leaving..."
                holder.trainColorIndicator
                    .setBackgroundColor(
                        Color.parseColor(it.hexColor))
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}