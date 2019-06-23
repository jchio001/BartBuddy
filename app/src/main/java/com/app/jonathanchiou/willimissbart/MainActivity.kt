package com.app.jonathanchiou.willimissbart

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.app.jonathanchiou.willimissbart.bottomnav.BottomNavigationView
import com.app.jonathanchiou.willimissbart.bottomnav.FragmentFactory
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripFragment
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment
import com.app.jonathanchiou.willimissbart.trips.createRealTimeTripFragment

class MainActivity : AppCompatActivity() {

    @BindView(R.id.bottom_navigationview)
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var tripManager: TripManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_edit)
        }

        StationsManager.initialize(this)

        tripManager = TripManager(PreferenceManager.getDefaultSharedPreferences(this))

        bottomNavigationView.setFragmentManager(
            supportFragmentManager,
            R.id.parent,
            object: FragmentFactory {

                override fun create(index: Int): Fragment {
                    return createRealTimeTripFragment(index == 1)
                }
            }
        )

        if (tripManager.originAbbreviation != null && tripManager.destinationAbbreviation != null) {
            bottomNavigationView.setSelection(0)
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
        if (!bottomNavigationView.onBackPressed()) {
            finish()
        }
    }
}