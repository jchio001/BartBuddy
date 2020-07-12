package com.app.jonathan.willimissbart.trips.models.internal

fun Map<String, String>.fullNameFromAbbr(abbr: String): String {
    return this[abbr] ?: throw MissingStationException(abbr)
}
