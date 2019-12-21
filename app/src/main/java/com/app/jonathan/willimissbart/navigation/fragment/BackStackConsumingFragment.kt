package com.app.jonathan.willimissbart.navigation.fragment

import androidx.annotation.LayoutRes
import com.app.jonathan.willimissbart.utils.view.ViewBindableFragment

abstract class BackStackConsumingFragment(@LayoutRes containerLayoutId: Int) : ViewBindableFragment(containerLayoutId) {

    abstract fun onBackPressed(): Boolean
}
