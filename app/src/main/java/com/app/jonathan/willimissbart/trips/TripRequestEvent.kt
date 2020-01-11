package com.app.jonathan.willimissbart.trips

class TripsRequestEvent(
    val originAbbreviation: String,
    val destinationAbbreviation: String
) {

    override fun equals(other: Any?): Boolean {
        if (other !is TripsRequestEvent) {
            return false
        }

        return originAbbreviation == other.originAbbreviation
            && destinationAbbreviation == other.destinationAbbreviation
    }
}
