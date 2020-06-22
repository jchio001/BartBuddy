package com.app.jonathan.willimissbart.store

import com.app.jonathan.willimissbart.BuildConfig
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.poll(
    interval: Long,
    timeUnit: TimeUnit
): Observable<T> {
    return this
        .repeatWhen { response ->
            response.flatMap {
                Observable.timer(30, BuildConfig.UPDATE_TIME_UNIT)
            }
        }
        .retryWhen { error ->
            error.flatMap {
                Observable.timer(30, BuildConfig.UPDATE_TIME_UNIT)
            }
        }
}
