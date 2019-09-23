package com.app.jonathanchiou.willimissbart.trips

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.viewbinding.bind

class RealTimeTripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val trainColorIndicator: View by bind(R.id.train_color_indicator)
    val departureTimeTextView: TextView by bind(R.id.destination_textview)
    val timeUntilArrivalTextView: TextView by bind(R.id.time_until_arrival_textview)

    fun bind(realTimeTrip: RealTimeTrip) {
        val firstWaitRealTimeLeg = realTimeTrip.realTimeLegs.first() as RealTimeLeg.Wait
        departureTimeTextView.text = "To ${firstWaitRealTimeLeg.nextTrainHeadStation}"
        timeUntilArrivalTextView.text =
            if (firstWaitRealTimeLeg.duration != 0) "${firstWaitRealTimeLeg.duration} min"
            else "Leaving..."
        trainColorIndicator.setBackgroundColor(Color.parseColor(realTimeTrip.hexColor))
    }
}
