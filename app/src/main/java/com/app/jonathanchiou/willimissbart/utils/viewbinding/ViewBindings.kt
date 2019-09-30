package com.app.jonathanchiou.willimissbart.utils.viewbinding

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

class bind<out T : View>(private val idResource: Int) {

    private var view: T? = null

    operator fun getValue(viewHolder: RecyclerView.ViewHolder, property: KProperty<*>): T {
        return view ?: viewHolder.itemView.findViewById<T>(idResource).also { this.view = it }
    }
}
