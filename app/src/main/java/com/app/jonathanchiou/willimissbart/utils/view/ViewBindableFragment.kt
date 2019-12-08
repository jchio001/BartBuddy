package com.app.jonathanchiou.willimissbart.utils.view

import android.view.View
import androidx.fragment.app.Fragment

abstract class ViewBindableFragment : Fragment() {

    protected val viewCache = HashMap<Int, View>()

    @Suppress("UNCHECKED_CAST")
    fun <T: View> bind(id: Int): Lazy<T> {
        if (viewCache[id] != null) {
            return lazyOf(viewCache[id] as T)
        }

        return lazy {
            val view = view!!.findViewById<T>(id)
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
                val view = view!!.findViewById<View>(id)!!
                viewCache[id] = view
                view
            }
                .setOnClickListener(debouncingOnClickListener)
        }
    }
}
