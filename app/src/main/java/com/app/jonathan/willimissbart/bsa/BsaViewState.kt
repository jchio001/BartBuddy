package com.app.jonathan.willimissbart.bsa

import com.app.jonathan.willimissbart.api.ignoreIfHandledException
import com.app.jonathanchiou.willimissbart.db.models.Bsa

class BsaViewState(
    val showProgressBar: Boolean,
    val showRecyclerView: Boolean,
    val bsas: List<Bsa>? = null,
    private val throwable: Throwable? = null
) {

    val unhandledException = throwable?.ignoreIfHandledException()
}
