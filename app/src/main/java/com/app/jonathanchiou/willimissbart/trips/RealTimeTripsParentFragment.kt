package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.navigation.bottomnav.BottomNavigationView
import com.app.jonathanchiou.willimissbart.navigation.bottomnav.FragmentFactory
import com.app.jonathanchiou.willimissbart.navigation.fragment.BackStackConsumingFragment

class RealTimeTripsParentFragment : BackStackConsumingFragment() {

    val title: TextView by bind(R.id.title)
    val bottomNavigationView: BottomNavigationView by bind(R.id.bottom_navigationview)

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindClick(R.id.edit_icon) {
            fragmentManager!!
                .beginTransaction()
                .hide(this)
                .add(R.id.parent, TripSelectionFragment())
                .show(TripSelectionFragment())
                .addToBackStack(null)
                .commit()
        }

        bottomNavigationView.setFragmentManager(
            childFragmentManager,
            R.id.container,
            object : FragmentFactory {

                override fun create(index: Int): Fragment {
                    return createRealTimeTripFragment(index == 1)
                }
            }
        )

        bottomNavigationView.setSelection(0)
    }

    override fun onBackPressed(): Boolean {
        return if (childFragmentManager.backStackEntryCount <= 1) {
            false
        } else {
            bottomNavigationView.onBackPressed()
            true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        bottomNavigationView.onHiddenChanged(hidden)
    }

    companion object {

        const val BACKSTACK_TAG = "trip_parent_fragment"
    }
}
