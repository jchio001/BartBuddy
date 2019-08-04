package com.app.jonathanchiou.willimissbart.utils.viewbinding

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

class bind<out T : View>(private val idResource: Int) {

    operator fun getValue(viewHolder: RecyclerView.ViewHolder, property: KProperty<*>): T {
        return viewHolder.itemView.findViewById(idResource)
    }

    operator fun getValue(fragment: Fragment, property: KProperty<*>): T {
        return fragment.view!!.findViewById(idResource)
    }

    operator fun getValue(activity: Activity, property: KProperty<*>): T {
        return activity.findViewById(idResource)
    }
}

inline fun Fragment.bindClick(vararg idResources: Int, crossinline doOnClick: () -> Unit) {
    if (idResources.isEmpty()) {
        return
    }

    val debouncingOnClickListener = object: DebouncingOnClickListener() {

        override fun doOnClick() {
            doOnClick()
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

    val debouncingOnClickListener = object: DebouncingOnClickListener() {

        override fun doOnClick() {
            doOnClick()
        }
    }

    for (idResource in idResources) {
        this.findViewById<View>(idResource)
            .setOnClickListener(debouncingOnClickListener)
    }
}