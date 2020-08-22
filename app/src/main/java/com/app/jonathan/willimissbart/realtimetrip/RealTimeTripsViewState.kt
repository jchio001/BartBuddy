package com.app.jonathan.willimissbart.realtimetrip

import android.content.res.Resources
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.api.ignoreIfHandledException
import com.app.jonathan.willimissbart.api.isHttpException

data class RealTimeTripsViewState constructor(
    val showProgressBar: Boolean,
    val showRecyclerView: Boolean,
    val realTimeTrips: List<RealTimeTrip>? = null,
    private val throwable: Throwable? = null
) {

    val showErrorText = (throwable != null)

    fun getErrorText(resource: Resources): String {
        return throwable?.let { throwable ->
            resource.getString(
                if (throwable.isHttpException()) {
                    R.string.real_time_trips_bart_api_error
                } else {
                    R.string.real_time_trips_network_error
                }
            )
        } ?: ""
    }

    val unhandledException = throwable?.ignoreIfHandledException()
}
