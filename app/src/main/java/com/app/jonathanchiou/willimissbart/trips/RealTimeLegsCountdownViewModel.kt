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
import java.util.concurrent.TimeUnit

class RealTimeLegsCountdownViewModel : ViewModel() {

    val realTimeLegsLiveData = MutableLiveData<List<RealTimeLeg>>()

    private val realTimeTripSubject = PublishSubject.create<RealTimeTrip>()

    private val diposable: Disposable

    init {
        diposable = realTimeTripSubject
            .switchMap { realTimeTrip ->
                val delayDurationInMilliSeconds = maxOf(
                    (System.currentTimeMillis() - realTimeTrip.lastUpdatedTime)
                        % BuildConfig.UPDATE_TIME_UNIT.toMillis(1),
                    BuildConfig.DELAY_TIME_UNIT.toMillis(BuildConfig.MINIMUM_DELAY)
                )

                Observable.interval(1, BuildConfig.UPDATE_TIME_UNIT)
                    .scan(realTimeTrip.realTimeLegs.decrement(1)) { realTimeLegs, _ ->
                        realTimeLegs.decrement(1)
                    }
                    .delay(delayDurationInMilliSeconds, TimeUnit.MILLISECONDS)
                    .startWith(realTimeTrip.realTimeLegs)
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
