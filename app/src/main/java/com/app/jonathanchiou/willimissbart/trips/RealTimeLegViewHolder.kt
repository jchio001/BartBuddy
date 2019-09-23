package com.app.jonathanchiou.willimissbart.trips

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind

sealed class RealTimeLegViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val realTimeLegInfo: TextView by bind(R.id.real_time_leg_info)
    val realTimeLegDuration: TextView by bind(R.id.real_time_leg_duration)

    class Train(itemView: View) : RealTimeLegViewHolder(itemView) {

        fun bind(trainRealTimeLeg: RealTimeLeg.Train) {
            realTimeLegInfo.text =
                "From ${trainRealTimeLeg.origin}, " +
                    "take train heading towards ${trainRealTimeLeg.trainHeadStation}. " +
                    "Exit at ${trainRealTimeLeg.destination}."

            val estimateInMinutes = trainRealTimeLeg.duration
            realTimeLegDuration.text =
                "Take the train leaving ${
                if (estimateInMinutes == 0) "now!"
                else "%d minutes!".format(estimateInMinutes)
                }"
        }
    }

    class Wait(itemView: View) : RealTimeLegViewHolder(itemView) {

        fun bind(waitRealTimeLeg: RealTimeLeg.Wait) {
            realTimeLegInfo.text =
                "At ${waitRealTimeLeg.station}, wait for the next train heading towards " +
                    "${waitRealTimeLeg.nextTrainHeadStation}."
            realTimeLegDuration.text = "Next train arriving in ${waitRealTimeLeg.duration} minutes!"
        }
    }
}
