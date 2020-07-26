package com.app.jonathan.willimissbart.realtimetrip

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.util.Consumer
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathan.willimissbart.utils.view.DebouncingOnClickListener
import com.app.jonathan.willimissbart.utils.view.ViewInflater

class RealTimeTripView(
    context: Context,
    attributeSet: AttributeSet
) : RelativeLayout(context, attributeSet),
    Consumer<RealTimeTrip> {

    interface Callbacks {
        fun onRealTimeTripClicked(realTimeTrip: RealTimeTrip)
    }

    init {
        inflate(context, R.layout.merge_real_time_trip_view, this)
    }

    private val trainColorIndicator: View = findViewById(R.id.train_color_indicator)
    private val departureTimeTextView: TextView = findViewById(R.id.destination_textview)
    private val timeUntilArrivalTextView: TextView = findViewById(R.id.time_until_arrival_textview)

    var callbacks: Callbacks? = null

    override fun accept(item: RealTimeTrip) {
        val firstWaitRealTimeLeg = item.realTimeLegs.first() as RealTimeLeg.Wait
        departureTimeTextView.text = "To ${firstWaitRealTimeLeg.nextTrainHeadStation}"
        timeUntilArrivalTextView.text =
            if (firstWaitRealTimeLeg.duration != 0) "${firstWaitRealTimeLeg.duration} min"
            else "Leaving..."
        trainColorIndicator.setBackgroundColor(Color.parseColor(item.hexColor))
        setOnClickListener(DebouncingOnClickListener {
            callbacks?.onRealTimeTripClicked(item)
        })
    }

    companion object : ViewInflater(R.layout.real_time_trip_view)
}
