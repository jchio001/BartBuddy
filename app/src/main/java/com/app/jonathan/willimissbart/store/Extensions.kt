package com.app.jonathan.willimissbart.store

import com.app.jonathanchiou.willimissbart.db.models.Station

fun List<Station>.containsAllStations(
    stationAbbrs: List<String>,
    stationNames: List<String>
): Boolean {
    var abbrCount = 0
    var nameCount = 0

    for (station in this) {
        if (station.abbr in stationAbbrs) {
            ++abbrCount
        }
        if (station.name in stationNames) {
            ++nameCount
        }
    }

    return abbrCount == stationAbbrs.count() && nameCount == stationNames.count()
}
