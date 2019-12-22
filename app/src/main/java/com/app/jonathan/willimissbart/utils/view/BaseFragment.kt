package com.app.jonathan.willimissbart.utils.view

import androidx.annotation.LayoutRes

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : ViewBindableFragment(contentLayoutId) {

    @Suppress("UNCHECKED_CAST")
    fun <T> argument(key: String) = lazy {
        (arguments?.get(key) ?: throw IllegalStateException("Missing key ${key}")) as T
    }

    open fun onBackPressed() = false
}
