package com.app.jonathan.willimissbart.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Station::class], version = 1)
abstract class BartBuddyDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
}
