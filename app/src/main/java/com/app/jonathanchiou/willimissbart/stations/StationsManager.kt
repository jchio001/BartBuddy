package com.app.jonathanchiou.willimissbart.stations

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.app.jonathanchiou.willimissbart.api.ApiClient
import com.app.jonathanchiou.willimissbart.utils.models.*
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StationsManager(
    private val sharedPreferences: SharedPreferences,
    private val apiClient: ApiClient) {

    private var stations: List<Station>? = null

    private val stationsListType by lazy {
        Types.newParameterizedType(List::class.java, Station::class.java)
    }

    fun getStations(): Observable<UiModel<List<Station>>> {
        if (stations == null) {
            return Observable.fromCallable {
                Optional(
                    sharedPreferences.getString(
                        CACHED_STATIONS_KEY,
                        null))
            }
                // Makes SharedPreferences fetching asynchronous!
                .subscribeOn(Schedulers.computation())
                .flatMap { cachedStations ->
                    if (cachedStations.value == null) {
                        apiClient
                            .bartService
                            .getStations()
                            .responseToUiModelStream()
                            .map {
                                if (it.state == State.DONE) {
                                    UiModel(
                                        state = it.state,
                                        data = it.data!!.root.stations.stations)
                                } else {
                                    UiModel(state = it.state)
                                }
                            }
                            .doOnNext {
                                // Confirmed that this is not happening on the UI thread by logging the current
                                // thread's name.
                                if (it.state == State.DONE) {
                                    val stations = it.data!!

                                    sharedPreferences
                                        .edit()
                                        .putString(
                                            CACHED_STATIONS_KEY,
                                            apiClient.moshi
                                                .adapter<List<Station>>(stationsListType)
                                                .toJson(stations))
                                        .apply()

                                    this.stations = stations
                                }
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    } else {
                        Observable.fromCallable {
                            apiClient.moshi.adapter<List<Station>>(stationsListType).fromJson(cachedStations.value)!!
                        }
                            .modelToUiModelStream()
                            .doOnNext {
                                this.stations = it.data!!
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    }
                }
        } else {
            return Observable.just(
                UiModel(
                    state = State.DONE,
                    data = stations!!
                )
            )
        }
    }

    companion object {
        const val CACHED_STATIONS_KEY = "cached_stations"

        private var INSTANCE: StationsManager? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE =
                    StationsManager(
                        PreferenceManager.getDefaultSharedPreferences(context),
                        ApiClient.INSTANCE
                    )
            }
        }

        fun get(): StationsManager {
            return INSTANCE!!
        }
    }
}