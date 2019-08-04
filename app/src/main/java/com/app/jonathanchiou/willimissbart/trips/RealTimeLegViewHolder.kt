package com.app.jonathanchiou.willimissbart.trips

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind

sealed class RealTimeLegViewHolder(itemView: View) : ViewHolder(itemView)

class DoneRealTimeLegViewHolder(itemView: View) : RealTimeLegViewHolder(itemView) {

    val legInfoTextView: TextView by bind(R.id.leg_info_textview)
    val nextTrainEstimateTextView: TextView by bind(R.id.next_train_estimate_textview)

    fun renderView(realTimeLeg: RealTimeLeg) {
        legInfoTextView.text =
            "From ${realTimeLeg.origin}, " +
                "take train heading towards ${realTimeLeg.trainHeadStation}. " +
                "Exit at ${realTimeLeg.destination}."

        val estimateInMinutes = realTimeLeg.estimate!!.minutes
        nextTrainEstimateTextView.text =
            "Next train is leaving ${
            if (estimateInMinutes == 0) "now!"
            else "%d minutes!".format(estimateInMinutes)
            }"
    }
}

class PendingRealTimeLegViewHolder(itemView: View) : RealTimeLegViewHolder(itemView)

class ErrorRealTimeLegViewHolder(itemView: View) : RealTimeLegViewHolder(itemView)