package com.app.jonathan.willimissbart.api

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.isHandledNetworkException() =
    this.isBartApiException() || this.isNetworkException()

fun Throwable.isBartApiException() =
    this is HttpException

fun Throwable.isNetworkException() =
    this is SocketTimeoutException || this is IOException

fun Throwable.ignoreIfHandledNetworkException() =
    if (this.isHandledNetworkException()) null else this
