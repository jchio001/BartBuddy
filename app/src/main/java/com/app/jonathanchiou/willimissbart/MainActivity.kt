package com.app.jonathanchiou.willimissbart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.parent, TripSelectionFragment())
            .commit()
    }
}