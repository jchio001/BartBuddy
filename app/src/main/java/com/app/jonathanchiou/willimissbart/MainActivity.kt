package com.app.jonathanchiou.willimissbart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripManager.Companion.TRIP_DESTINATION_ABBREVIATION_KEY
import com.app.jonathanchiou.willimissbart.trips.TripManager.Companion.TRIP_ORIGIN_ABBREVIATION_KEY
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment
import com.app.jonathanchiou.willimissbart.trips.TripsFragment

class MainActivity : AppCompatActivity() {

    lateinit var tripManager: TripManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        tripManager = TripManager(PreferenceManager.getDefaultSharedPreferences(this))

        if (tripManager.originAbbreviation != null && tripManager.destinationAbbreviation != null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, TripsFragment())
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, TripSelectionFragment())
                .commit()
        }
    }
}