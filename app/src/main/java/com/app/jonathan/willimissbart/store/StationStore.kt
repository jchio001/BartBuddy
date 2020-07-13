package com.app.jonathan.willimissbart.store

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.db.Station
import com.app.jonathan.willimissbart.db.StationDao
import com.app.jonathan.willimissbart.stations.models.api.ApiStation
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationStore @Inject constructor(
    private val bartService: BartService,
    private val stationDao: StationDao
) {

    fun getStations(
        forceRefresh: Boolean = false,
        failSafelyWithCache: Boolean = false
    ): Single<List<Station>> {
        val fetchAndCacheStationsSingle = bartService.getStations()
            .map { bartStationsResponse ->
                bartStationsResponse.root.stations.apiStations.map(ApiStation::toDbModel)
            }
            .subscribeOn(Schedulers.io())
            .flatMap { stations ->
                stationDao
                    .insertStations(stations)
                    .andThen(Single.just(stations))
                    .subscribeOn(Schedulers.io())
            }
            .onErrorResumeNext { throwable ->
                if (failSafelyWithCache) {
                    stationDao.getStations()
                        .subscribeOn(Schedulers.io())
                        // TODO: Use a map instead?
                        .flatMap { stations ->
                            if (stations.isEmpty()) {
                                Single.error(throwable)
                            } else {
                                Single.just(stations)
                            }
                        }
                } else {
                    Single.error(throwable)
                }
            }

        return if (forceRefresh) {
            fetchAndCacheStationsSingle
        } else {
            stationDao
                .getStations()
                .flatMap { cachedStations ->
                    if (cachedStations.isEmpty()) {
                        fetchAndCacheStationsSingle
                    } else {
                        Single.just(cachedStations)
                    }
                }
        }
    }

    fun getStationsWithNamesAndAbbrs(
        abbrs: List<String>,
        names: List<String>
    ): Single<List<Station>> {
        return stationDao
            .getStationsWithNamesAndAbbrs(
                abbrs = abbrs,
                names = names
            )
    }
}
