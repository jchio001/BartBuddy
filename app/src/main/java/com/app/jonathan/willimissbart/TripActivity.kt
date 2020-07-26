package com.app.jonathan.willimissbart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTripsParentFragment
import com.app.jonathan.willimissbart.trips.TripManager
import com.app.jonathan.willimissbart.trips.TripSelectionFragment
import com.app.jonathan.willimissbart.utils.view.BaseFragment
import javax.inject.Inject

class TripActivity : AppCompatActivity() {

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
                    .add(
                        R.id.parent,
                        RealTimeTripsParentFragment.newInstance(),
                        RealTimeTripsParentFragment.BACKSTACK_TAG
                    )
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .add(
                        R.id.parent,
                        TripSelectionFragment.newInstance(),
                        TripSelectionFragment.BACKSTACK_TAG
                    )
                    .commit()
            }
        }
    }

    override fun onBackPressed() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                if (!(fragment as BaseFragment).onBackPressed()) {
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
