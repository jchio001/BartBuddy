package com.app.jonathan.willimissbart.stations

import com.app.jonathan.willimissbart.db.Station

class StationSelectionViewState(
    val showProgressBar: Boolean,
    val showErrorTextView: Boolean,
    val showStationsRecyclerView: Boolean,
    val stations: List<Station>? = null,
    val throwable: Throwable? = null
)
