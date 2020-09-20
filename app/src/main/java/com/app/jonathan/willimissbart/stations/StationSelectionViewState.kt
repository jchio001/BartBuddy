package com.app.jonathan.willimissbart.stations

import com.app.jonathanchiou.willimissbart.db.models.Station

class StationSelectionViewState(
    val showProgressBar: Boolean,
    val showErrorTextView: Boolean,
    val showStationsRecyclerView: Boolean,
    val stations: List<Station>? = null,
    val throwable: Throwable? = null
)
