package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.BasicDiffCallback

class RealTimeLegPagerAdapter :
    ListAdapter<RealTimeLeg, CompleteRealTimeLegViewHolder>(
        BasicDiffCallback<RealTimeLeg>()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CompleteRealTimeLegViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_real_time_leg, parent, false)
    )

    override fun onBindViewHolder(holder: CompleteRealTimeLegViewHolder, position: Int) =
        holder.renderView(getItem(position))
}
