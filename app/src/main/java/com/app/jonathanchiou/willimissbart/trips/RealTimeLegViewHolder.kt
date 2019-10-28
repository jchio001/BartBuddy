package com.app.jonathanchiou.willimissbart.trips

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind

sealed class RealTimeLegViewHolder<T : RealTimeLeg>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    enum class State {
        COMPLETED,
        IN_PROGRESS,
        TODO;

        companion object {

            fun toState(currentIndex: Int, focusedIndex: Int): State {
                return if (currentIndex < focusedIndex) {
                    COMPLETED
                } else if (currentIndex == focusedIndex) {
                    IN_PROGRESS
                } else {
                    TODO
                }
            }
        }
    }

    val realTimeLegInfo: TextView by bind(R.id.real_time_leg_info_text)
    val realTimeLegDuration: TextView by bind(R.id.real_time_leg_duration_text)

    abstract fun bind(realTimeLeg: T, state: State)

    class Train(itemView: View) : RealTimeLegViewHolder<RealTimeLeg.Train>(itemView) {

        override fun bind(realTimeLeg: RealTimeLeg.Train, state: State) {
            realTimeLegInfo.text =
                "From ${realTimeLeg.origin}, " +
                    "take train heading towards ${realTimeLeg.trainHeadStation}. " +
                    "Exit at ${realTimeLeg.destination}."
            realTimeLegDuration.text =
                if (realTimeLeg.duration > 0) {
                    "The train will take approximately ${realTimeLeg.duration} minutes."
                } else if (realTimeLeg.duration == 0) {
                    "The train has arrived!"
                } else {
                    "The train has passed ${realTimeLeg.destination}."
                }
        }
    }

    class Wait(itemView: View) : RealTimeLegViewHolder<RealTimeLeg.Wait>(itemView) {

        override fun bind(realTimeLeg: RealTimeLeg.Wait, state: State) {
            realTimeLegInfo.text =
                "At ${realTimeLeg.station}, look for the next train heading towards " +
                    "${realTimeLeg.nextTrainHeadStation}."
            realTimeLegDuration.text = if (realTimeLeg.duration > 0) {
                "Estimate wait of ${realTimeLeg.duration} minutes."
            } else if (realTimeLeg.duration == 0) {
                "The train has arrived!"
            } else {
                "The train has passed ${realTimeLeg.station}."
            }
        }
    }
}
