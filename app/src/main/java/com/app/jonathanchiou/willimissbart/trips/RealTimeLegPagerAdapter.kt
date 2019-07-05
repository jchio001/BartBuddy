package com.app.jonathanchiou.willimissbart.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg

class RealTimeLegPagerAdapter(private val realTimeLegs: List<RealTimeLeg>): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.layout_real_time_leg, container, false)

        view.findViewById<TextView>(R.id.leg_info_textview).text =
            "From ${realTimeLegs[position].origin}, " +
                "take train heading towards ${realTimeLegs[position].trainHeadStation}. " +
                "Exit at ${realTimeLegs[position].destination}."

        val estimate = realTimeLegs[position].etds[0].estimates[0].minutes
        view.findViewById<TextView>(R.id.next_train_estimate_textview).text =
            "Next train is leaving ${if (estimate == 0) "now!" else "%d minutes!".format(estimate)}"

        container.addView(view, 0)
        return view
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
}