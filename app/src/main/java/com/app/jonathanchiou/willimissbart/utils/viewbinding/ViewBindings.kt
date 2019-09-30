package com.app.jonathanchiou.willimissbart.utils.viewbinding

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

class bind<out T : View>(private val idResource: Int) {

    private var view: T? = null

    operator fun getValue(viewHolder: RecyclerView.ViewHolder, property: KProperty<*>): T {
        return view ?: viewHolder.itemView.findViewById<T>(idResource).also { this.view = it }
    }

    operator fun getValue(fragment: Fragment, property: KProperty<*>): T {
        return fragment.view!!.findViewById<T>(idResource).also { this.view = it }
    }

    operator fun getValue(activity: Activity, property: KProperty<*>): T {
        return activity.findViewById<T>(idResource).also { this.view = it }
    }
}

inline fun Fragment.bindClick(vararg idResources: Int, crossinline doOnClick: (View) -> Unit) {
    if (idResources.isEmpty()) {
        return
    }

    val debouncingOnClickListener = object : DebouncingOnClickListener() {

        override fun doOnClick(view: View) {
            doOnClick(view)
        }
    }

    for (idResource in idResources) {
        this.view!!.findViewById<View>(idResource)
            .setOnClickListener(debouncingOnClickListener)
    }
}

inline fun Activity.bindClick(vararg idResources: Int, crossinline doOnClick: () -> Unit) {
    if (idResources.isEmpty()) {
        return
    }

    val debouncingOnClickListener = object : DebouncingOnClickListener() {

        override fun doOnClick(view: View) {
            doOnClick(view)
        }
    }

    for (idResource in idResources) {
        this.findViewById<View>(idResource)
            .setOnClickListener(debouncingOnClickListener)
    }
}
