package com.app.jonathanchiou.willimissbart.db

import android.content.Context
import androidx.room.Room
import com.app.jonathanchiou.willimissbart.db.dao.BsaDao
import com.app.jonathanchiou.willimissbart.db.dao.StationDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @[JvmStatic Provides Singleton]
    fun provideBartBuddyDatabase(
        context: Context
    ): BartBuddyDatabase {
        return Room.databaseBuilder(
            context,
            BartBuddyDatabase::class.java,
            "bart_buddy_database"
        )
            .addMigrations(*Migrations.get())
            .fallbackToDestructiveMigration()
            .build()
    }

    @[JvmStatic Provides Singleton]
    fun provideBsaDao(
        bartBuddyDatabase: BartBuddyDatabase
    ): BsaDao {
        return bartBuddyDatabase.bsaDao()
    }

    @[JvmStatic Provides Singleton]
    fun provideStationDao(
        bartBuddyDatabase: BartBuddyDatabase
    ): StationDao {
        return bartBuddyDatabase.stationDao()
    }
}
