package com.app.jonathanchiou.willimissbart.utils.viewbinding

import android.view.View

abstract class DebouncingOnClickListener : View.OnClickListener {

    private var isBeingClicked = false

    override fun onClick(v: View?) {
        if (!isBeingClicked) {
            isBeingClicked = true
            doOnClick()
            isBeingClicked = false
        }
    }

    abstract fun doOnClick()
}
