package com.app.jonathanchiou.willimissbart.trips

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind

class CompleteRealTimeLegViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    val legInfoTextView: TextView by bind(R.id.leg_info_textview)
    val nextTrainEstimateTextView: TextView by bind(R.id.next_train_estimate_textview)

    fun renderView(completeLeg: RealTimeLeg) {
        legInfoTextView.text =
            "From ${completeLeg.origin}, " +
                "take train heading towards ${completeLeg.trainHeadStation}. " +
                "Exit at ${completeLeg.destination}."

        val estimateInMinutes = completeLeg.estimate.minutes
        nextTrainEstimateTextView.text =
            "Take the train leaving ${
            if (estimateInMinutes == 0) "now!"
            else "%d minutes!".format(estimateInMinutes)
            }"
    }
}
