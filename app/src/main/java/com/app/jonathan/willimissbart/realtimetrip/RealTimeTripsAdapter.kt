package com.app.jonathan.willimissbart.realtimetrip

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.jonathan.willimissbart.utils.BasicDiffCallback
import com.app.jonathan.willimissbart.utils.view.GenericViewHolder

class RealTimeTripsAdapter :
    ListAdapter<RealTimeTrip, GenericViewHolder>(BasicDiffCallback<RealTimeTrip>()),
    RealTimeTripView.Callbacks {

    interface Callbacks {
        fun onRealTimeTripClicked(realTimeTrip: RealTimeTrip)
    }

    var callbacks: Callbacks? = null

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
