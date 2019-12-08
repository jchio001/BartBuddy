package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.BasicDiffCallback

class RealTimeLegAdapter : ListAdapter<RealTimeLeg, RealTimeLegViewHolder<RealTimeLeg>>(
    BasicDiffCallback<RealTimeLeg>()
) {

    private var focusedIndex = 0

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is RealTimeLeg.Train -> 0
        is RealTimeLeg.Wait -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealTimeLegViewHolder<RealTimeLeg> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_real_time_leg, parent, false)
        return when (viewType) {
            0 -> RealTimeLegViewHolder.Train(view)
            1 -> RealTimeLegViewHolder.Wait(view)
            else -> throw IllegalStateException("Invalid view type")
        } as RealTimeLegViewHolder<RealTimeLeg>
    }

    override fun onBindViewHolder(holder: RealTimeLegViewHolder<RealTimeLeg>, position: Int) {
        holder.bind(getItem(position), RealTimeLegViewHolder.State.toState(position, focusedIndex))
    }

    override fun submitList(list: List<RealTimeLeg>?) {
        if (list != null) {
            var newFocusedIndex = 0
            while (newFocusedIndex < list.size && list[newFocusedIndex].duration < 0) {
                ++newFocusedIndex
            }
            focusedIndex = newFocusedIndex
        }

        super.submitList(list)
    }
}
