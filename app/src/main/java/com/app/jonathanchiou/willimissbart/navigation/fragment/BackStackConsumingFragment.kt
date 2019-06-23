package com.app.jonathanchiou.willimissbart.navigation.fragment

import androidx.fragment.app.Fragment

abstract class BackStackConsumingFragment: Fragment() {

    abstract fun onBackPressed(): Boolean
}