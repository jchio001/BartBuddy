package com.app.jonathan.willimissbart.store

import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.apimodels.station.ApiStation
import com.app.jonathanchiou.willimissbart.db.dao.StationDao
import com.app.jonathanchiou.willimissbart.db.models.Station
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
                bartStationsResponse.stations.apiStations.map(ApiStation::toDbModel)
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
        stationAbbrs: List<String>,
        stationNames: List<String>
    ): Single<List<Station>> {
        return stationDao
            .getStationsWithNamesAndAbbrs(
                stationAbbrs = stationAbbrs,
                stationNames = stationNames
            )
            .subscribeOn(Schedulers.io())
            .flatMap { cachedStations ->
                if (
                    cachedStations.containsAllStations(
                        stationAbbrs = stationAbbrs,
                        stationNames = stationNames
                    )
                ) {
                    Single.just(cachedStations)
                } else {
                    getStations(forceRefresh = true)
                        .map { updatedStations ->
                            if (
                                updatedStations.containsAllStations(
                                    stationAbbrs = stationAbbrs,
                                    stationNames = stationNames
                                )
                            ) {
                                updatedStations
                            } else {
                                throw MissingStationsException(
                                    stationAbbrs = stationAbbrs,
                                    stationNames = stationNames,
                                    cachedStations = cachedStations,
                                    updatedStations = updatedStations
                                )
                            }
                        }
                }
            }
    }
}
