package com.app.jonathan.willimissbart.api

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.isHandledNetworkException(): Boolean {
    return this is HttpException || this is SocketTimeoutException || this is IOException
}
