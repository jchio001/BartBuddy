package com.app.jonathan.willimissbart.utils.view

import android.view.View

fun View.changeVisibilityAndEnable(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.INVISIBLE
    this.isEnabled = show
}

var View.isVisible: Boolean
    get() = (visibility == View.VISIBLE)
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.GONE
    }
