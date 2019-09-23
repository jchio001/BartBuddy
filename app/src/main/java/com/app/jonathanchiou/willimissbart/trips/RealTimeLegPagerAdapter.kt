package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.BasicDiffCallback
import java.lang.IllegalStateException

class RealTimeLegPagerAdapter :
    ListAdapter<RealTimeLeg, RealTimeLegViewHolder>(
        BasicDiffCallback<RealTimeLeg>()
    ) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is RealTimeLeg.Train -> 0
        is RealTimeLeg.Wait -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RealTimeLegViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_real_time_leg, parent, false);
        return when (viewType) {
            0 -> RealTimeLegViewHolder.Train(view)
            1 -> RealTimeLegViewHolder.Wait(view)
            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RealTimeLegViewHolder, position: Int) {
        when (holder) {
            is RealTimeLegViewHolder.Train -> holder.renderView(getItem(position) as RealTimeLeg.Train)
            is RealTimeLegViewHolder.Wait -> holder.renderView(getItem(position) as RealTimeLeg.Wait)
        }
    }
}
