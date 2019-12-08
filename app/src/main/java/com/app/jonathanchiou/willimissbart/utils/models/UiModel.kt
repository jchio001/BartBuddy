package com.app.jonathanchiou.willimissbart.utils.models

import io.reactivex.Observable
import io.reactivex.exceptions.CompositeException
import retrofit2.Response
import java.net.HttpURLConnection

fun <QUERY, RESULT> Observable<RESULT>.modelToUiModelStream(query: QUERY? = null): Observable<UiModel<QUERY, RESULT>> {
    return this.map {
        UiModel(
            state = State.DONE,
            query = query,
            statusCode = HttpURLConnection.HTTP_OK,
            data = it)
    }
        .handlePendingAndError()
}

fun <QUERY, RESULT> Observable<Response<RESULT>>.toTerminalUiModelStream(query: QUERY? = null):
    Observable<UiModel<QUERY, RESULT>> {
    return this
        .map {
            if (it.isSuccessful) {
                UiModel<QUERY, RESULT>(
                    state = State.DONE,
                    query = query,
                    statusCode = it.code(),
                    data = it.body())
            } else {
                UiModel(
                    state = State.ERROR,
                    query = query,
                    statusCode = it.code())
            }
        }
        .handleError()
}

private fun <QUERY, RESULT> Observable<UiModel<QUERY, RESULT>>.handlePendingAndError(query: QUERY? = null):
    Observable<UiModel<QUERY, RESULT>> {
    return this.onErrorReturn {
        UiModel(
            state = State.ERROR,
            error = it)
    }
        .startWith(
            UiModel(
                query = query,
                state = State.PENDING))
}

private fun <QUERY, RESULT> Observable<UiModel<QUERY, RESULT>>.handleError(): Observable<UiModel<QUERY, RESULT>> {
    return this.onErrorReturn {
        UiModel(
            state = State.ERROR,
            error = it)
    }
}

data class UiModel<QUERY, RESULT>(
    val state: State,
    val query: QUERY? = null,
    val statusCode: Int? = null,
    val data: RESULT? = null,
    val error: Throwable? = null
)
