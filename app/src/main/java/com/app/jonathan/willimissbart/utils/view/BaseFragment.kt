package com.app.jonathan.willimissbart.utils.view

import android.os.Bundle
import androidx.annotation.LayoutRes

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : ViewBindableFragment(contentLayoutId) {

    @Suppress("UNCHECKED_CAST")
    fun <T> argument(key: String) = lazy {
        (arguments?.get(key) ?: throw IllegalStateException("Missing key ${key}")) as T
    }

    inline fun setArguments(argumentSupplier: Bundle.() -> Unit): BaseFragment {
        val bundle = Bundle()
        argumentSupplier(bundle)
        arguments = bundle
        return this
    }

    open fun onBackPressed() = false
}
