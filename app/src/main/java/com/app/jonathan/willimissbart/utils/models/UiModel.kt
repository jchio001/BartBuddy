package com.app.jonathan.willimissbart.utils.models

data class UiModel<QUERY, RESULT>(
    val state: State,
    val query: QUERY? = null,
    val data: RESULT? = null,
    val error: Throwable? = null
)
