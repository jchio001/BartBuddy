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
            data = it
        )
    }
        .handlePendingAndError()
}

private fun <QUERY, RESULT> Observable<UiModel<QUERY, RESULT>>.handlePendingAndError(query: QUERY? = null):
    Observable<UiModel<QUERY, RESULT>> {
    return this.errorToUiModel()
        .startWith(
            UiModel(
                query = query,
                state = State.PENDING
            )
        )
}

fun <QUERY, RESULT> Observable<UiModel<QUERY, RESULT>>.errorToUiModel(): Observable<UiModel<QUERY, RESULT>> {
    return this.onErrorReturn {
        UiModel(
            state = State.ERROR,
            error = it
        )
    }
}

data class UiModel<QUERY, RESULT>(
    val state: State,
    val query: QUERY? = null,
    val data: RESULT? = null,
    val error: Throwable? = null
)
