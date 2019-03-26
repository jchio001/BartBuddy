package com.app.jonathanchiou.willimissbart

import io.reactivex.Observable
import retrofit2.Response

enum class State {
    ERROR,
    PENDING,
    DONE,
}

fun <T> Observable<Response<T>>.toUiModelObservable(): Observable<UiModel<T>> {
    return this
        .map {
            if (it.isSuccessful) {
                UiModel(
                    state = State.DONE,
                    data = it.body())
            } else {
                UiModel(state = State.ERROR)
            }
        }
        .onErrorReturn {
            UiModel(
                state = State.ERROR,
                error = it)
        }
        .startWith(UiModel(state = State.PENDING))
}

data class UiModel<T>(val state: State,
                      val data: T? = null,
                      val error: Throwable? = null)