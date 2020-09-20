package com.app.jonathanchiou.willimissbart.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.jonathanchiou.willimissbart.db.converters.DateTypeConverter
import com.app.jonathanchiou.willimissbart.db.dao.BsaDao
import com.app.jonathanchiou.willimissbart.db.dao.StationDao
import com.app.jonathanchiou.willimissbart.db.models.Bsa
import com.app.jonathanchiou.willimissbart.db.models.Station

@Database(
    entities = [Station::class, Bsa::class],
    exportSchema = true,
    version = 2)
@TypeConverters(DateTypeConverter::class)
abstract class BartBuddyDatabase : RoomDatabase() {

    abstract fun bsaDao(): BsaDao
    abstract fun stationDao(): StationDao
}
