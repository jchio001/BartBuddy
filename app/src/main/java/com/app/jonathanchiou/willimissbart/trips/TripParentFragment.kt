package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.navigation.bottomnav.BottomNavigationView
import com.app.jonathanchiou.willimissbart.navigation.bottomnav.FragmentFactory
import com.app.jonathanchiou.willimissbart.navigation.fragment.BackStackConsumingFragment

class TripParentFragment: BackStackConsumingFragment() {

    @BindView(R.id.title)
    lateinit var title: TextView

    @BindView(R.id.bottom_navigationview)
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        bottomNavigationView.setFragmentManager(
            childFragmentManager,
            R.id.container,
            object: FragmentFactory {

                override fun create(index: Int): Fragment {
                    return createRealTimeTripFragment(index == 1)
                }
            }
        )

        bottomNavigationView.setSelection(0)
        onHiddenChanged(false)
    }

    override fun onBackPressed(): Boolean {
        if (childFragmentManager.backStackEntryCount <= 1) {
            return false
        } else {
            bottomNavigationView.onBackPressed()
            return true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        for (fragment in childFragmentManager.fragments) {
            fragment.onHiddenChanged(hidden)
        }
    }

    @OnClick(R.id.edit_icon)
    fun onEditIconClicked() {
        fragmentManager!!
            .beginTransaction()
            .hide(this)
            .add(R.id.parent, TripSelectionFragment())
            .show(TripSelectionFragment())
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val BACKSTACK_TAG = "trip_parent_fragment"
    }
}