package com.app.jonathanchiou.willimissbart.navigation.fragment

import com.app.jonathanchiou.willimissbart.utils.viewbinding.ViewBindableFragment

abstract class BackStackConsumingFragment : ViewBindableFragment() {

    abstract fun onBackPressed(): Boolean
}
