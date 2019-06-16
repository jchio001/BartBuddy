package com.app.jonathanchiou.willimissbart.utils.models

import io.reactivex.Observable
import retrofit2.Response
import java.net.HttpURLConnection

enum class State {
    ERROR,
    PENDING,
    DONE,
}

inline fun <T, V> Response<T>.mapResponse(mapFunc: (T) -> V): Response<V> {
    return if (this.isSuccessful) {
        Response.success(mapFunc(this.body()!!))
    } else {
        Response.error(this.code(), this.errorBody())
    }
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

fun <T> Observable<Response<T>>.toTerminalUiModelStream(): Observable<UiModel<T>> {
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
        .handleError()
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

private fun <T> Observable<UiModel<T>>.handleError(): Observable<UiModel<T>> {
    return this.
        onErrorReturn {
            UiModel(
                state = State.ERROR,
                error = it)
        }
}

data class UiModel<T>(val state: State,
                      val statusCode: Int? = null,
                      val data: T? = null,
                      val error: Throwable? = null) {

    companion object {

        fun <T> zip(uiModels: List<UiModel<T>>): UiModel<List<T>> {
            val modelList = ArrayList<T>(uiModels.size)
            var lowestState = State.DONE

            for (uiModel in uiModels) {
                uiModel.state.let {
                    if (it.ordinal < lowestState.ordinal) {
                        lowestState = it
                    }
                }

                uiModel.data?.also { modelList.add(it) }
            }

            return UiModel(
                state = lowestState,
                data = if (lowestState == State.DONE) modelList else null)
        }
    }
}