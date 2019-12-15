package com.app.jonathanchiou.willimissbart.stations

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.stations.models.api.Station
import com.app.jonathanchiou.willimissbart.utils.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationsManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val bartService: BartService,
    private val moshi: Moshi
) {

    val stationsLiveData = MutableLiveData<UiModel<Void, List<Station>>>()

    private val stationsRequestSubject = PublishSubject.create<Any>()

    val stationsListType = Types.newParameterizedType(List::class.java, Station::class.java)

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
                            bartService
                                .getStations()
                                .map { it.root.stations.stations }
                                .modelToUiModelStream<Void, List<Station>>()
                                .doOnNext { stationsUiModel ->
                                    // Confirmed that this is not happening on the UI thread by logging the current
                                    // thread's name.
                                    if (stationsUiModel.state == State.DONE) {
                                        val stations = stationsUiModel.data!!

                                        sharedPreferences
                                            .edit()
                                            .putString(
                                                CACHED_STATIONS_KEY,
                                                moshi
                                                    .adapter<List<Station>>(stationsListType)
                                                    .toJson(stations))
                                            .apply()
                                    }
                                }
                                .errorToUiModel()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        } else {
                            Observable.fromCallable {
                                moshi.adapter<List<Station>>(stationsListType).fromJson(cachedStations.value)!!
                            }
                                .modelToUiModelStream<Void, List<Station>>()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        }
                    }
            }
            .subscribe(stationsLiveData::postValue)
    }

    fun getStationsFromLocalStorage(): List<Station> {
        return stationsLiveData.value?.data ?: moshi.adapter<List<Station>>(stationsListType)
            .fromJson(
                sharedPreferences.getString(
                    CACHED_STATIONS_KEY,
                    null)!!)!!.also {
            stationsLiveData.postValue(
                UiModel(
                    state = State.DONE,
                    data = it
                )
            )
        }
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
    }
}
