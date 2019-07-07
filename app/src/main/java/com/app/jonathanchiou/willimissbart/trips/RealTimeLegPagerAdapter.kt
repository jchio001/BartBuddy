package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.models.State
import io.reactivex.functions.BiConsumer

class RealTimeLegPagerAdapter(private val realTimeLegs: MutableList<RealTimeLeg>): PagerAdapter() {

    var onRequestRealTimeLeg: BiConsumer<RealTimeLeg, Int>? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realTimeLeg = realTimeLegs[position]

        val realTimeLegView = when (realTimeLeg.state) {
            State.PENDING -> {
                val view = LayoutInflater.from(container.context)
                    .inflate(R.layout.layout_real_time_leg_pending, container, false)

                onRequestRealTimeLeg?.accept(realTimeLeg, position)
                view
            }
            State.DONE -> {
                val view = LayoutInflater.from(container.context)
                    .inflate(R.layout.layout_real_time_leg, container, false)
                view.findViewById<TextView>(R.id.leg_info_textview).text =
                    "From ${realTimeLeg.origin}, " +
                        "take train heading towards ${realTimeLeg.trainHeadStation}. " +
                        "Exit at ${realTimeLeg.destination}."

                val estimate = realTimeLeg.etds[0].estimates[0].minutes
                view.findViewById<TextView>(R.id.next_train_estimate_textview).text =
                    "Next train is leaving ${if (estimate == 0) "now!" else "%d minutes!".format(estimate)}"
                view
            }
            State.ERROR -> {
                val view = LayoutInflater.from(container.context)
                    .inflate(R.layout.layout_real_time_leg_error, container, false)
                view
            }
        }

        container.addView(realTimeLegView, 0)
        return realTimeLegView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return realTimeLegs.size
    }

    fun updateItem(realTimeLeg: RealTimeLeg, position: Int) {
        realTimeLegs[position] = realTimeLeg
        notifyDataSetChanged()
    }
}