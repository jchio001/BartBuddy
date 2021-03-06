package com.app.jonathan.willimissbart.utils.view

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class ViewBindableActivity(@LayoutRes containerLayoutId: Int) : AppCompatActivity(containerLayoutId) {

    protected val viewCache = HashMap<Int, View>(5)

    @Suppress("UNCHECKED_CAST")
    fun <T : View> bind(id: Int): Lazy<T> {
        if (viewCache[id] != null) {
            return lazyOf(viewCache[id] as T)
        }

        return lazy {
            val view = findViewById<T>(id)
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
                val view = findViewById<View>(id)!!
                viewCache[id] = view
                view
            }
                .setOnClickListener(debouncingOnClickListener)
        }
    }
}
