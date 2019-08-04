package com.app.jonathanchiou.willimissbart.application

import android.content.Context
import kotlin.reflect.KProperty

abstract class ComponentDelegate<T> {

    abstract val serviceName: String

    operator fun getValue(thisRef: Context, property: KProperty<*>): T {
        return thisRef.applicationContext.getSystemService(serviceName) as T
    }
}