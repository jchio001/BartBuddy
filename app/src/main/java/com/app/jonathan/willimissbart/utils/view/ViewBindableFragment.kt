package com.app.jonathan.willimissbart.utils.view

import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class ViewBindableFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected val viewCache = HashMap<Int, View>()

    @Suppress("UNCHECKED_CAST")
    fun <T : View> bind(id: Int): Lazy<T> {
        if (viewCache[id] != null) {
            return lazyOf(viewCache[id] as T)
        }

        return lazy {
            val view = view!!.findViewById<T>(id)
            viewCache[id] = view
            view
        }
    }

    fun bindClick(vararg ids: Int, doOnClick: (View) -> Unit) {
        if (ids.isEmpty()) {
            return
        }

        val debouncingOnClickListener = DebouncingOnClickListener(doOnClick)

        for (id in ids) {
            if (viewCache[id] != null) {
                viewCache[id]!!
            } else {
                val view = view!!.findViewById<View>(id)!!
                viewCache[id] = view
                view
            }
                .setOnClickListener(debouncingOnClickListener)
        }
    }
}
