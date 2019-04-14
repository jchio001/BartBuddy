package com.app.jonathanchiou.willimissbart

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.app.jonathanchiou.willimissbart.trips.TripFragment
import com.app.jonathanchiou.willimissbart.trips.TripManager
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment

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
                .add(R.id.parent, TripFragment())
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.parent, TripSelectionFragment())
                .commit()
        }
    }
}