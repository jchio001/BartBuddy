package com.app.jonathan.willimissbart.trips.models.internal

class MissingStationException(
    stationAbbr: String
) : Exception() {

    override val message = "Station with abbreviation $stationAbbr not available locally."
}
