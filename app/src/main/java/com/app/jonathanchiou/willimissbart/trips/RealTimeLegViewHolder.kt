package com.app.jonathanchiou.willimissbart.trips

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg

sealed class RealTimeLegViewHolder(itemView: View): ViewHolder(itemView)

class DoneRealTimeLegViewHolder(itemView: View): RealTimeLegViewHolder(itemView) {

    @BindView(R.id.leg_info_textview)
    lateinit var legInfoTextView: TextView

    @BindView(R.id.next_train_estimate_textview)
    lateinit var nextTrainEstimateTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

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

class PendingRealTimeLegViewHolder(itemView: View): RealTimeLegViewHolder(itemView)

class ErrorRealTimeLegViewHolder(itemView: View): RealTimeLegViewHolder(itemView)