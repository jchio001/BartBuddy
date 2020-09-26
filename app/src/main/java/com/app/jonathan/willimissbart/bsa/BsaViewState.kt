package com.app.jonathan.willimissbart.bsa

import com.app.jonathan.willimissbart.api.ignoreIfHandledException
import com.app.jonathanchiou.willimissbart.api.models.bsa.ApiBsa

class BsaViewState(
    val showProgressBar: Boolean,
    val showRecyclerView: Boolean,
    val bsas: List<ApiBsa>? = null,
    throwable: Throwable? = null
) {

    val unhandledException = throwable?.ignoreIfHandledException()
}
