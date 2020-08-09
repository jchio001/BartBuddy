package com.app.jonathan.willimissbart.utils.view

import androidx.annotation.LayoutRes

abstract class BaseActivity(@LayoutRes containerLayoutId: Int) : ViewBindableActivity(containerLayoutId) {

    @Suppress("UNCHECKED_CAST")
    fun <T> extra(key: String) = lazy {
        (intent?.extras?.get(key) ?: throw IllegalStateException("Missing key ${key}")) as T
    }
}
