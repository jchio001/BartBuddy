package com.app.jonathanchiou.willimissbart

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripFragment
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment

class MainActivity : AppCompatActivity() {

    lateinit var tripManager: TripManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        StationsManager.initialize(this)

        tripManager = TripManager(PreferenceManager.getDefaultSharedPreferences(this))
        if (tripManager.originAbbreviation != null && tripManager.destinationAbbreviation != null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, RealTimeTripFragment())
                .commit()
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
}