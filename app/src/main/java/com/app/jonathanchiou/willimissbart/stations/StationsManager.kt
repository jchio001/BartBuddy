package com.app.jonathanchiou.willimissbart.stations

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.api.ApiClient
import com.app.jonathanchiou.willimissbart.utils.models.*
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class StationsManager(
    private val sharedPreferences: SharedPreferences,
    private val apiClient: ApiClient) {

    val stationsLiveData =  MutableLiveData<UiModel<List<Station>>>()

    private val stationsRequestSubject = PublishSubject.create<Any>()

    private val stationsListType by lazy {
        Types.newParameterizedType(List::class.java, Station::class.java)
    }

    init {
        stationsRequestSubject
            .switchMap {
                Observable.fromCallable {
                    Optional(
                        sharedPreferences.getString(
                            CACHED_STATIONS_KEY,
                            null))
                }
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
                                    }
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        } else {
                            Observable.fromCallable {
                                apiClient.moshi.adapter<List<Station>>(stationsListType).fromJson(cachedStations.value)!!
                            }
                                .modelToUiModelStream()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        }
                    }
            }
            .subscribe(stationsLiveData::postValue)
    }

    fun requestStations() {
        stationsLiveData.value.also {
            if (it == null || it.state == State.ERROR) {
                stationsRequestSubject.onNext(Any())
            }
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