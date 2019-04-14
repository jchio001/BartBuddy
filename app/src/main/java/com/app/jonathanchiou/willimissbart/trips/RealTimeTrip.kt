package com.app.jonathanchiou.willimissbart.trips

data class RealTimeTrip(val originAbbreviation: String,
                        val destinationAbbreviation: String,
                        val originEtds: List<Etd>,
                        val destinationEtds: List<Etd>)