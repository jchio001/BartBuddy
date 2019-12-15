package com.app.jonathan.willimissbart.trips.models.internal

sealed class Union<FIRST, SECOND> {

    class First<FIRST, SECOND>(val value: FIRST) : Union<FIRST, SECOND>()

    class Second<FIRST, SECOND>(val value: SECOND) : Union<FIRST, SECOND>()

    companion object {
        fun <FIRST, SECOND> first(value: FIRST): Union<FIRST, SECOND> = First(value)
        fun <FIRST, SECOND> second(value: SECOND): Union<FIRST, SECOND> = Second(value)
    }
}
