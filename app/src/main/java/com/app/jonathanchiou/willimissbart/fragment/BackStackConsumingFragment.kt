package com.app.jonathanchiou.willimissbart.fragment

import androidx.fragment.app.Fragment

abstract class BackStackConsumingFragment: Fragment() {

    abstract fun onBackPressed(): Boolean
}