package com.app.jonathan.willimissbart.trips

import com.app.jonathan.willimissbart.trips.MissingStationException

fun Map<String, String>.fullNameFromAbbr(abbr: String): String {
    return this[abbr] ?: throw MissingStationException(abbr)
}
