package com.app.jonathan.willimissbart.trips

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.navigation.bottomnav.BottomNavigationView
import com.app.jonathan.willimissbart.navigation.bottomnav.FragmentFactory
import com.app.jonathan.willimissbart.utils.view.BaseFragment

class RealTimeTripsParentFragment : BaseFragment(R.layout.fragment_trip_parent) {

    val title: TextView by bind(R.id.title)
    val bottomNavigationView: BottomNavigationView by bind(R.id.bottom_navigationview)

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
