package com.app.jonathanchiou.willimissbart.utils.view

import android.view.View

abstract class DebouncingOnClickListener : View.OnClickListener {

    private var isBeingClicked = false

    override fun onClick(view: View) {
        if (!isBeingClicked) {
            isBeingClicked = true
            doOnClick(view)
            isBeingClicked = false
        }
    }

    abstract fun doOnClick(view: View)
}
