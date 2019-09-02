package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.models.State
import io.reactivex.functions.BiConsumer

class RealTimeLegPagerAdapter(private val realTimeLegs: MutableList<RealTimeLeg>) : Adapter<RealTimeLegViewHolder>() {

    var onRequestRealTimeLeg: BiConsumer<RealTimeLeg, Int>? = null

    override fun getItemCount(): Int {
        return realTimeLegs.size
    }

    override fun getItemViewType(position: Int): Int {
        return realTimeLegs[position].state.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealTimeLegViewHolder {
        return when (viewType) {
            State.DONE.ordinal -> {
                RealTimeLegViewHolder.DoneRealTimeLegViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_real_time_leg, parent, false))
            }
            State.PENDING.ordinal -> {
                RealTimeLegViewHolder.PendingRealTimeLegViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_real_time_leg_pending, parent, false))
            }
            State.ERROR.ordinal -> {
                RealTimeLegViewHolder.ErrorRealTimeLegViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_real_time_leg_error, parent, false))
            }
            else -> throw IllegalStateException("")
        }
    }

    override fun onBindViewHolder(holder: RealTimeLegViewHolder, position: Int) {
        when (holder) {
            is RealTimeLegViewHolder.DoneRealTimeLegViewHolder ->
                holder.renderView(realTimeLegs[position])
            is RealTimeLegViewHolder.PendingRealTimeLegViewHolder ->
                onRequestRealTimeLeg?.accept(realTimeLegs[position], position)
        }
    }

    fun updateItem(realTimeLeg: RealTimeLeg, position: Int) {
        realTimeLegs[position] = realTimeLeg
        notifyItemChanged(position)
    }
}
