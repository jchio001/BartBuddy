package com.app.jonathanchiou.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.stations.StationsManager
import com.app.jonathanchiou.willimissbart.trips.models.api.Etd
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapBody
import com.app.jonathanchiou.willimissbart.utils.models.modelToUiModelStream
import com.app.jonathanchiou.willimissbart.utils.models.responseToUiModelStream
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class RealTimeLegViewModel(stationsManager: StationsManager,
                           bartService: BartService) : ViewModel() {

    val realTimeLegLiveData = MutableLiveData<UiModel<Void, RealTimeTrip>>()

    private val realTimeLegSubject = PublishSubject.create<RealTimeTrip>()

    private val diposable: Disposable

    init {
        diposable = realTimeLegSubject
            .switchMap { realTimeTrip ->
                if (realTimeTrip.isIncomplete) {
                    val realTimeLegs = realTimeTrip.realTimeLegs
                    val incompleteLeg = realTimeLegs.last() as RealTimeLeg.Incomplete

                    bartService.getRealTimeEstimates(incompleteLeg.origin)
                        .mapBody {
                            val station = stationsManager
                                .getStationsFromLocalStorage()
                                .filter { it.abbr == incompleteLeg.trainHeadStation }
                                .first()

                            val firstEstimate = it.root.etdStations[0].etds
                                .filter {
                                    it.abbreviation == station.abbr
                                }
                                .map(Etd::estimates)
                                .first()
                                .first()

                            realTimeTrip.completeLeg(
                                RealTimeLeg.Complete(
                                    incompleteLeg.origin,
                                    incompleteLeg.destination,
                                    incompleteLeg.trainHeadStation,
                                    firstEstimate
                                )
                            )
                        }
                        .responseToUiModelStream<Void, RealTimeTrip>()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                } else {
                    Observable.just(realTimeTrip)
                        .modelToUiModelStream()
                }
            }
            .subscribe(realTimeLegLiveData::postValue)
    }

    fun completeRealTimeTrip(realTimeTrip: RealTimeTrip) {
        realTimeLegSubject.onNext(realTimeTrip)
    }
}
