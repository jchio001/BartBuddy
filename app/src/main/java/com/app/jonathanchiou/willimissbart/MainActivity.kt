package com.app.jonathanchiou.willimissbart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        StationsManager.initialize(this)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.parent, TripSelectionFragment())
            .commit()
    }
}