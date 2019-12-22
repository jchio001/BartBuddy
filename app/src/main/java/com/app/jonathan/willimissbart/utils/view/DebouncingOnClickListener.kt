package com.app.jonathan.willimissbart.utils.view

import android.view.View

class DebouncingOnClickListener(private val doOnClick: (View) -> Unit) : View.OnClickListener {

    private var isBeingClicked = false

    override fun onClick(view: View) {
        if (!isBeingClicked) {
            isBeingClicked = true
            doOnClick(view)
            isBeingClicked = false
        }
    }
}
