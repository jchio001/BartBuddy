package com.app.jonathanchiou.willimissbart.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.jonathanchiou.willimissbart.db.models.Station
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stations: List<Station>): Completable

    @Query("Select * from Station")
    fun getStations(): Single<List<Station>>

    @Query(
        """
        Select * from Station
        where 
        abbr IN(:stationAbbrs)
        or
        name IN(:stationNames)
        """
    )
    fun getStationsWithNamesAndAbbrs(
        stationAbbrs: List<String>,
        stationNames: List<String>
    ): Single<List<Station>>
}
