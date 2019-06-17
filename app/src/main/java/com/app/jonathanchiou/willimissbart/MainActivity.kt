package com.app.jonathanchiou.willimissbart

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.BottomNavigationViewManager.FragmentFactory
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripFragment
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    @BindView(R.id.bottom_navigationview)
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var tripManager: TripManager

    lateinit var bottomNavigationViewManager: BottomNavigationViewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        supportActionBar?.hide()

        StationsManager.initialize(this)

        tripManager = TripManager(PreferenceManager.getDefaultSharedPreferences(this))

        bottomNavigationViewManager = BottomNavigationViewManager(
            bottomNavigationView,
            supportFragmentManager,
            R.id.parent,
            object: FragmentFactory {

                override fun createFragment(itemId: Int): Fragment {
                    return RealTimeTripFragment()
                }
            }
        )

        if (tripManager.originAbbreviation != null && tripManager.destinationAbbreviation != null) {
            bottomNavigationViewManager.setSelectedItem(R.id.trips)
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, TripSelectionFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val tripSelectionFragment = TripSelectionFragment()

            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.fragments[0])
                .add(R.id.parent, tripSelectionFragment)
                .show(tripSelectionFragment)
                .addToBackStack("TripSelectionFragment")
                .commit()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!bottomNavigationViewManager.onBackPressed()) {
            finish()
        }
    }
}