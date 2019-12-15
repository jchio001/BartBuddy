package com.app.jonathan.willimissbart.navigation.fragment

import com.app.jonathan.willimissbart.utils.view.ViewBindableFragment

abstract class BackStackConsumingFragment : ViewBindableFragment() {

    abstract fun onBackPressed(): Boolean
}
