package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg

class RealTimeLegPagerAdapter : RecyclerView.Adapter<RealTimeLegViewHolder>() {

    private var realTimeLegs = listOf<RealTimeLeg>()

    override fun getItemCount() = realTimeLegs.size

    override fun getItemViewType(position: Int) = when (realTimeLegs[position]) {
        is RealTimeLeg.Train -> 0
        is RealTimeLeg.Wait -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealTimeLegViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_real_time_leg, parent, false)
        return when (viewType) {
            0 -> RealTimeLegViewHolder.Train(view)
            1 -> RealTimeLegViewHolder.Wait(view)
            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RealTimeLegViewHolder, position: Int) {
        when (holder) {
            is RealTimeLegViewHolder.Train -> holder.bind(realTimeLegs[position] as RealTimeLeg.Train)
            is RealTimeLegViewHolder.Wait -> holder.bind(realTimeLegs[position] as RealTimeLeg.Wait)
        }
    }

    fun submitList(updatedRealTimeLegs: List<RealTimeLeg>) {
        if (itemCount != updatedRealTimeLegs.size) {
            this.realTimeLegs = updatedRealTimeLegs
            notifyItemRemoved(0)
        } else {
            this.realTimeLegs = updatedRealTimeLegs
            notifyItemChanged(0)
        }
    }
}
