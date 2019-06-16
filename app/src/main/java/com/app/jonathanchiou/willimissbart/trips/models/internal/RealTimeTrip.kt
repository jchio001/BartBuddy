package com.app.jonathanchiou.willimissbart.trips.models.internal

import com.app.jonathanchiou.willimissbart.trips.models.api.Etd

data class RealTimeTrip(val originAbbreviation: String,
                        val destinationAbbreviation: String,
                        val originEtds: List<Etd>)