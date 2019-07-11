package com.app.jonathanchiou.willimissbart.utils.models

import com.app.jonathanchiou.willimissbart.utils.GulpException
import io.reactivex.Observable
import retrofit2.Response
import java.net.HttpURLConnection

inline fun <T, V> Observable<Response<T>>.mapBody(crossinline mapFunc: (T) -> V): Observable<Response<V>> {
    return this
        .map {
            if (it.isSuccessful) {
                Response.success(mapFunc(it.body()!!))
            } else {
                Response.error(it.code(), it.errorBody())
            }
        }
}

fun <QUERY, RESULT> Observable<RESULT>.modelToUiModelStream(query: QUERY? = null): Observable<UiModel<QUERY, RESULT>> {
    return this.
        map {
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
                UiModel(
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

fun <QUERY, RESULT> Observable<Response<RESULT>>.responseToUiModelStream(query: QUERY? = null):
    Observable<UiModel<QUERY, RESULT>> {
    return this
        .map {
            if (it.isSuccessful) {
                UiModel(
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
        .handlePendingAndError(query)
}

private fun <QUERY, RESULT> Observable<UiModel<QUERY, RESULT>>.handlePendingAndError(query: QUERY? = null):
    Observable<UiModel<QUERY, RESULT>> {
    return this.
        onErrorReturn {
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
    return this.
        onErrorReturn {
            UiModel(
                state = State.ERROR,
                error = it)
        }
}

data class UiModel<QUERY, RESULT>(val state: State,
                                  val query: QUERY? = null,
                                  val statusCode: Int? = null,
                                  val data: RESULT? = null,
                                  val error: Throwable? = null) {

    companion object {

        fun <QUERY, RESULT> zipAndFlatten(uiModels: List<UiModel<QUERY, List<RESULT>>>): UiModel<QUERY, List<RESULT>> {
            val throwables = ArrayList<Throwable?>(uiModels.size)
            var lowestState = State.DONE
            var exceptionThrown = false

            for (uiModel in uiModels) {
                uiModel.state.let {
                    if (it.ordinal < lowestState.ordinal) {
                        lowestState = it
                    }
                }

                uiModel.error?.also {
                    exceptionThrown = true
                    throwables.add(uiModel.error)
                }
            }

            val flattenedModelsList =
                if (lowestState == State.DONE) {
                    uiModels
                        .map(UiModel<QUERY, List<RESULT>>::data)
                        .filterNotNull()
                        .flatten()
                } else {
                    null
                }

            return UiModel(
                state = lowestState,
                query = uiModels[0].query,
                data = flattenedModelsList,
                error = if (exceptionThrown) GulpException(throwables) else null)
        }
    }
}