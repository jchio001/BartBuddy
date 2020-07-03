package com.app.jonathan.willimissbart.store

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.db.StationDao
import com.app.jonathan.willimissbart.stations.models.api.ApiStation
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationStore @Inject constructor(
    private val bartService: BartService,
    private val stationDao: StationDao,
    private val sharedPreferences: SharedPreferences
) {

    @SuppressLint("CheckResult")
    fun refresh(force: Boolean = false) {
        if (
            force ||
            !haveFetchedData() ||
            (getTimeSinceLastUpdate() >= (BuildConfig.UPDATE_TIME_UNIT.toMillis(60)))
        ) {
            bartService.getStations()
                .map { bartStationsResponse ->
                    bartStationsResponse.root.stations.apiStations.map(ApiStation::toDbModel)
                }
                .subscribeOn(Schedulers.io())
                .flatMapCompletable { stations ->
                    sharedPreferences.edit()
                        .putLong(
                            Station::class.java.toString(),
                            System.currentTimeMillis()
                        )
                        .apply()

                    stationDao.insertStations(stations)
                }
                .subscribe()
        }
    }

    private fun haveFetchedData() = sharedPreferences.contains(Station::class.java.toString())

    private fun getTimeSinceLastUpdate(): Long {
        return System.currentTimeMillis() -
            sharedPreferences.getLong(
                Station::class.java.toString(),
                System.currentTimeMillis()
            )
    }

    fun stream(): Observable<List<Station>> {
        return stationDao
            .getStations()
    }
}
