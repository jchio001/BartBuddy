package com.app.jonathanchiou.willimissbart.utils.viewbinding

import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class ViewBindableActivity : AppCompatActivity() {

    protected val viewCache = HashMap<Int, View>(5)

    @Suppress("UNCHECKED_CAST")
    fun <T: View> bind(id: Int): Lazy<T> {
        if (viewCache[id] != null) {
            return lazyOf(viewCache[id] as T)
        }

        return lazy {
            val view = findViewById<T>(id)
            viewCache[id] = view
            view
        }
    }

    inline fun bindClick(vararg ids: Int, crossinline doOnClick: (View) -> Unit) {
        if (ids.isEmpty()) {
            return
        }

        val debouncingOnClickListener = object : DebouncingOnClickListener() {

            override fun doOnClick(view: View) {
                doOnClick(view)
            }
        }

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
