package com.app.jonathanchiou.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.models.api.Etd
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapBody
import com.app.jonathanchiou.willimissbart.utils.models.responseToUiModelStream
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class RealTimeLegRequestEvent(val index: Int,
                              val originAbbreviation: String,
                              val destinationAbbreviation: String,
                              val trainHeadStationAbbrevation: String)

class RealTimeLegViewModel(stationsManager: StationsManager,
                           bartService: BartService) : ViewModel() {

    val realTimeLegLiveData = MutableLiveData<UiModel<Int, RealTimeLeg>>()

    private val realTimeLegSubject = PublishSubject.create<RealTimeLegRequestEvent>()

    private val diposable: Disposable

    init {
        diposable = realTimeLegSubject
            .switchMap { requestEvent ->
                bartService.getRealTimeEstimates(requestEvent.originAbbreviation)
                    .mapBody {
                        val station = stationsManager
                            .getStationsFromLocalStorage()
                            .filter { it.abbr == requestEvent.trainHeadStationAbbrevation }
                            .first()

                        val firstEstimate = it.root.etdStations[0].etds
                            .filter {
                                it.abbreviation == station.abbr
                            }
                            .map(Etd::estimates)
                            .first()
                            .first()

                        RealTimeLeg(
                            State.DONE,
                            requestEvent.originAbbreviation,
                            requestEvent.destinationAbbreviation,
                            requestEvent.trainHeadStationAbbrevation,
                            firstEstimate)
                    }
                    .responseToUiModelStream(requestEvent.index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(realTimeLegLiveData::postValue)
    }

    fun requestRealTimeLeg(realTimeLeg: RealTimeLeg, index: Int) {
        realTimeLegSubject
            .onNext(
                RealTimeLegRequestEvent(
                    index,
                    realTimeLeg.origin,
                    realTimeLeg.destination,
                    realTimeLeg.trainHeadStation))
    }
}
