package com.app.jonathan.willimissbart.utils.view

import android.view.View

var View.isVisible: Boolean
    get() = (visibility == View.VISIBLE)
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.GONE
    }
