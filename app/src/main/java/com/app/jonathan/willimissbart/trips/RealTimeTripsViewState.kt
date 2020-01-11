package com.app.jonathan.willimissbart.trips

import com.app.jonathan.willimissbart.trips.models.internal.RealTimeTrip

data class RealTimeTripsViewState constructor(
    val showProgressBar: Boolean,
    val showRecyclerView: Boolean,
    val realTimeTrips: List<RealTimeTrip>? = null,
    val throwable: Throwable? = null
)
