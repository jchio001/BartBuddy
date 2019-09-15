package com.app.jonathanchiou.willimissbart

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.navigation.fragment.BackStackConsumingFragment
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripsParentFragment
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var tripManager: TripManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appComponent.inject(this)

        supportActionBar?.hide()

        if (supportFragmentManager.backStackEntryCount == 0) {
            if (tripManager.getOriginAbbreviation() != null && tripManager.getDestinationAbbreviation() != null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.parent, RealTimeTripsParentFragment(), RealTimeTripsParentFragment.BACKSTACK_TAG)
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.parent, TripSelectionFragment(), TripSelectionFragment.BACKSTACK_TAG)
                    .commit()
            }
        }
    }

    override fun onBackPressed() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                if (!(fragment as BackStackConsumingFragment).onBackPressed()) {
                    if (supportFragmentManager.backStackEntryCount >= 1) {
                        supportFragmentManager.popBackStack()
                    } else {
                        finish()
                    }
                }
            }
        }
    }
}
