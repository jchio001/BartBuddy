package com.app.jonathan.willimissbart.utils.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

open class ViewInflater(@LayoutRes val layoutRes: Int) {

    fun inflate(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(
                layoutRes,
                parent,
                false
            )
    }
}
