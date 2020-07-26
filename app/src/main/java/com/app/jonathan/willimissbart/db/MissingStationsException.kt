package com.app.jonathan.willimissbart.db

class MissingStationsException(
    stationAbbrs: List<String>,
    stationNames: List<String>,
    cachedStations: List<Station>,
    updatedStations: List<Station>
) : Exception() {

    override val message = """
        Failed to fetch missing stations from BART's API. Something is seriously wrong!"
        Station abbreviations: $stationAbbrs
        Station names: $stationNames
        Cached stations: $cachedStations
        Updated stations from a fresh API request: $updatedStations 
    """.trimIndent()
}
