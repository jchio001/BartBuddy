package com.app.jonathan.willimissbart.realtimetrip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathan.willimissbart.trips.models.internal.decrement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class RealTimeLegsCountdownViewModel : ViewModel() {

    val realTimeLegsLiveData = MutableLiveData<List<RealTimeLeg>>()

    private val realTimeTripSubject = PublishSubject.create<RealTimeTrip>()

    private val disposable: Disposable

    init {
        disposable = realTimeTripSubject
            .switchMap { realTimeTrip ->
                val updateTimeUnitInMillis = BuildConfig.UPDATE_TIME_UNIT.toMillis(1)
                val delayDurationInMilliSeconds = maxOf(
                    (updateTimeUnitInMillis - (System.currentTimeMillis() - realTimeTrip.lastUpdatedTime))
                        % BuildConfig.UPDATE_TIME_UNIT.toMillis(1),
                    BuildConfig.DELAY_TIME_UNIT.toMillis(BuildConfig.MINIMUM_DELAY)
                )

                Observable.interval(1, BuildConfig.UPDATE_TIME_UNIT)
                    .scan(realTimeTrip.realTimeLegs.decrement(1)) { realTimeLegs, _ ->
                        realTimeLegs.decrement(1)
                    }
                    .delay(delayDurationInMilliSeconds, TimeUnit.MILLISECONDS)
                    .startWith(realTimeTrip.realTimeLegs)
                    .takeUntil { realTimeLegs ->
                        realTimeLegs.last().duration < 0
                    }
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(realTimeLegsLiveData::postValue)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun seed(realTimeTrip: RealTimeTrip) = realTimeTripSubject.onNext(realTimeTrip)
}
