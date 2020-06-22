package com.app.jonathan.willimissbart.store

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.db.StationDao
import com.app.jonathan.willimissbart.stations.models.api.ApiStation
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationStore @Inject constructor(
    private val bartService: BartService,
    private val stationDao: StationDao,
    private val sharedPreferences: SharedPreferences
) {

    @SuppressLint("CheckResult")
    fun poll(refresh: Boolean = false) {
        bartService.getStations()
            .map { bartStationsResponse ->
                bartStationsResponse.root.stations.apiStations
            }
            .map { stations ->
                stations.map(ApiStation::toDbModel)
            }
            .run {
                if (
                    (System.currentTimeMillis() -
                        sharedPreferences.getLong(Station::class.java.toString(), 0) >=
                            (30 * BuildConfig.UPDATE_TIME_UNIT.toMillis(1))) &&
                    !refresh
                ) {
                    this.delay(30, BuildConfig.UPDATE_TIME_UNIT)
                } else {
                    this
                }
            }
            .poll(30, BuildConfig.UPDATE_TIME_UNIT)
            .flatMapCompletable { stations ->
                stationDao.insertStations(stations)
            }
    }

    fun stream(): Observable<List<Station>> {
        return stationDao
            .getStations()
    }
}
