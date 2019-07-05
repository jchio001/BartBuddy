package com.app.jonathanchiou.willimissbart.api

import com.app.jonathanchiou.willimissbart.trips.TripRequestEvent
import com.app.jonathanchiou.willimissbart.trips.models.api.Trip
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.mapBody
import com.app.jonathanchiou.willimissbart.utils.models.toTerminalUiModelStream
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BartIntegerAdapter {

    @FromJson
    fun fromJson(value: String): Int {
        return try {
            Integer.parseInt(value)
        } catch (e: NumberFormatException) {
            0
        }
    }

    @ToJson
    fun toJson(value: Int): String {
        return value.toString()
    }
}

fun BartService.getEtdsForTrips(tripRequestEvent: TripRequestEvent,
                                trips: List<Trip>):
    Observable<UiModel<TripRequestEvent, List<RealTimeTrip>>> {
    val etdObservables = trips
        .map { trip ->
            this.getRealTimeEstimates(trip.legs[0].origin)
                .mapBody { etdRootWrapper ->
                    val firstLeg = trip.legs[0]
                    val filteredEtds = etdRootWrapper.root.etdStations[0].etds
                        .filter {
                            trip.legs[0].trainHeadStation.contains(it.destination)
                        }

                    val realTimeLegs = ArrayList<RealTimeLeg>(trip.legs.size)
                    realTimeLegs.add(
                        RealTimeLeg(
                            State.DONE,
                            firstLeg.origin,
                            firstLeg.destination,
                            filteredEtds[0].abbreviation,
                            filteredEtds))

                    for (i in 1 until trip.legs.size) {
                        realTimeLegs.add(
                            RealTimeLeg(
                                State.PENDING,
                                trip.legs[i].origin,
                                trip.legs[i].destination,
                                "",
                                emptyList()
                            )
                        )
                    }

                    RealTimeTrip(
                        trip.origin,
                        trip.destination,
                        realTimeLegs)
                }
                .toTerminalUiModelStream(query = tripRequestEvent)
        }

    return Observable
        .zip(etdObservables) { objects ->
            UiModel.zip(objects.map { it as UiModel<TripRequestEvent, RealTimeTrip> })
                .let { realTimeTripUiModel ->
                    if (realTimeTripUiModel.data != null ) {
                        realTimeTripUiModel.copy(
                            data = realTimeTripUiModel.data
                                .filter { it.realTimeLegs.isNotEmpty() })
                    } else {
                        realTimeTripUiModel
                    }
                }
        }
}

class ApiClient {

    val moshi by lazy {
        Moshi.Builder()
            .add(BartIntegerAdapter())
            .add(SingleToListFactory())
            .build()
    }

    val bartService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.bart.gov/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build())
            .build()
            .create(BartService::class.java)
    }

    companion object {
        val INSTANCE by lazy {
            ApiClient()
        }
    }
}