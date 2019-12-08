package com.app.jonathanchiou.willimissbart.trips

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathanchiou.willimissbart.BuildConfig
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.trips.models.internal.decrement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class RealTimeLegsCountdownViewModel: ViewModel() {

    val realTimeLegsLiveData = MutableLiveData<List<RealTimeLeg>>()

    private val realTimeTripSubject = PublishSubject.create<RealTimeTrip>()

    private val diposable: Disposable

    init {
        diposable = realTimeTripSubject
            .switchMap { realTimeTrip ->
                val elapsedMilliseconds = System.currentTimeMillis() - realTimeTrip.lastUpdatedTime
                val timeUnitElapsed = elapsedMilliseconds / BuildConfig.UPDATE_TIME_UNIT_MILLISECONDS

                Observable.interval(1, BuildConfig.UPDATE_TIME_UNIT)
                    .scan(realTimeTrip.realTimeLegs.decrement(timeUnitElapsed.toInt() + 1)) { realTimeLegs, _ ->
                        realTimeLegs.decrement(1)
                    }
                    .startWith(realTimeTrip.realTimeLegs.decrement(timeUnitElapsed.toInt()))
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(realTimeLegsLiveData::postValue)
    }

    override fun onCleared() {
        super.onCleared()
        diposable.dispose()
    }

    fun seed(realTimeTrip: RealTimeTrip) = realTimeTripSubject.onNext(realTimeTrip)
}
