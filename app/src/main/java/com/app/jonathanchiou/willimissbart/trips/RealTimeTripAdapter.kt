package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.BasicDiffCallback

class RealTimeTripAdapter : ListAdapter<RealTimeTrip, RealTimeTripViewHolder>(
    BasicDiffCallback<RealTimeTrip>()
) {

    var onClickListener: Consumer<RealTimeTrip>? = null

    private var isClicked = false

    private lateinit var recyclerView: RecyclerView

    private val debouncedOnClickListener = View.OnClickListener {
        if (!isClicked) {
            isClicked = true
            onClickListener?.accept(getItem(recyclerView.getChildLayoutPosition(it)))
            isClicked = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealTimeTripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_real_time_trip, parent, false)
        view.setOnClickListener(debouncedOnClickListener)
        return RealTimeTripViewHolder(view)
    }

    override fun onBindViewHolder(holder: RealTimeTripViewHolder, position: Int) = holder.bind(getItem(position))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}
