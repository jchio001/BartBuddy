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
import com.app.jonathanchiou.willimissbart.fragment.BackStackConsumingFragment
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.*

class MainActivity : AppCompatActivity() {

    lateinit var tripManager: TripManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        supportActionBar?.hide()

        StationsManager.initialize(this)

        tripManager = TripManager(PreferenceManager.getDefaultSharedPreferences(this))

        if (tripManager.originAbbreviation != null && tripManager.destinationAbbreviation != null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, TripParentFragment())
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, TripSelectionFragment())
                .commit()
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