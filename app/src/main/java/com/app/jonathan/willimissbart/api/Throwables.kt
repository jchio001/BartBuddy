package com.app.jonathan.willimissbart.api

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.exceptions.CompositeException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.isHandledException(): Boolean {
    return this.isHttpException() ||
        this is SocketTimeoutException ||
        this is IOException ||
        ((this as? CompositeException)?.areAllExceptionsHandled() ?: false)
}

fun Throwable.isHttpException() =
    this is HttpException

fun CompositeException.areAllExceptionsHandled(): Boolean {
    return this.exceptions.fold(true) { onlyHandledExceptions, throwable ->
        onlyHandledExceptions && throwable.isHandledException()
    }
}

fun Throwable.ignoreIfHandledException() =
    if (this.isHandledException()) null else this
