package com.app.jonathanchiou.willimissbart.trips

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.BasicDiffCallback
import com.app.jonathanchiou.willimissbart.utils.view.GenericViewHolder

class RealTimeTripsAdapter :
    ListAdapter<RealTimeTrip, GenericViewHolder>(BasicDiffCallback<RealTimeTrip>()),
    RealTimeTripView.Callbacks {

    interface Callbacks {
        fun onRealTimeTripClicked(realTimeTrip: RealTimeTrip)
    }

    var callbacks: Callbacks? = null

    private var isClicked = false

    private lateinit var recyclerView: RecyclerView

    private val debouncedOnClickListener = View.OnClickListener {
        if (!isClicked) {
            isClicked = true
            callbacks?.onRealTimeTripClicked(getItem(recyclerView.getChildLayoutPosition(it)))
            isClicked = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenericViewHolder(RealTimeTripView.inflate(parent))

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder.itemView as RealTimeTripView).also { realTimeTripView ->
            realTimeTripView.accept(getItem(position))
            realTimeTripView.callbacks = this
        }
    }

    override fun onRealTimeTripClicked(realTimeTrip: RealTimeTrip) {
        callbacks?.onRealTimeTripClicked(realTimeTrip)
    }
}
