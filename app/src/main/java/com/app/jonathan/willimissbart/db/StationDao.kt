package com.app.jonathan.willimissbart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<Station>): Completable

    @Query("Select * from Station")
    fun getStations(): Observable<List<Station>>
}
