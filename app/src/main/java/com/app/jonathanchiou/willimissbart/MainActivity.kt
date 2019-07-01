package com.app.jonathanchiou.willimissbart

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.navigation.fragment.BackStackConsumingFragment
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripsParentFragment
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment

class MainActivity : AppCompatActivity() {

    lateinit var tripManager: TripManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        supportActionBar?.hide()

        StationsManager.initialize(this)

        tripManager = TripManager(PreferenceManager.getDefaultSharedPreferences(this))

        if (supportFragmentManager.backStackEntryCount == 0) {
            if (tripManager.originAbbreviation != null && tripManager.destinationAbbreviation != null) {
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