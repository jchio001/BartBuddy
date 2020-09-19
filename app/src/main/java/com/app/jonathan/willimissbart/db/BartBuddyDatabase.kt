package com.app.jonathan.willimissbart.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Station::class, Bsa::class],
    exportSchema = true,
    version = 2)
@TypeConverters(DateTypeConverter::class)
abstract class BartBuddyDatabase : RoomDatabase() {

    abstract fun bsaDao(): BsaDao
    abstract fun stationDao(): StationDao
}
