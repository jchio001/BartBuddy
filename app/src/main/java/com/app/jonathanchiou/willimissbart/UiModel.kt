package com.app.jonathanchiou.willimissbart

import io.reactivex.Observable
import retrofit2.Response
import java.net.HttpURLConnection

enum class State {
    ERROR,
    PENDING,
    DONE,
}

fun <T> Observable<T>.modelToUiModelStream(): Observable<UiModel<T>> {
    return this.
        map {
            UiModel(
                state = State.DONE,
                statusCode = HttpURLConnection.HTTP_OK,
                data = it)
        }
        .handlePendingAndError()
}

fun <T> Observable<Response<T>>.responseToUiModelStream(): Observable<UiModel<T>> {
    return this
        .map {
            if (it.isSuccessful) {
                UiModel(
                    state = State.DONE,
                    statusCode = it.code(),
                    data = it.body())
            } else {
                UiModel(
                    state = State.ERROR,
                    statusCode = it.code())
            }
        }
        .handlePendingAndError()
}

private fun <T> Observable<UiModel<T>>.handlePendingAndError(): Observable<UiModel<T>> {
    return this.
        onErrorReturn {
            UiModel(
                state = State.ERROR,
                error = it)
        }
        .startWith(
            UiModel(state = State.PENDING))
}

data class UiModel<T>(val state: State,
                      val statusCode: Int? = null,
                      val data: T? = null,
                      val error: Throwable? = null)