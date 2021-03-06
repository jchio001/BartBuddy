package com.app.jonathan.willimissbart.api

import com.app.jonathan.willimissbart.BuildConfig
import com.app.jonathanchiou.willimissbart.api.models.bsa.ApiBsaRoot
import com.app.jonathanchiou.willimissbart.api.models.etd.ApiEtdRoot
import com.app.jonathanchiou.willimissbart.api.models.station.ApiStationsRoot
import com.app.jonathanchiou.willimissbart.api.models.trip.ApiTripsRoot
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BartService {

    @GET("stn.aspx?cmd=stns&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getStations(): Single<ApiStationsRoot>

    // Link to the documentation since this endpoint's a mess: https://api.bart.gov/docs/sched/depart.aspx
    @GET("sched.aspx?cmd=depart&date=now&b=0&a=2&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getDepartures(
        @Query("orig") orig: String,
        @Query("dest") dest: String
    ): Single<ApiTripsRoot>

    @GET("etd.aspx?cmd=etd&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getRealTimeEstimates(@Query("orig") origin: String): Observable<ApiEtdRoot>

    @GET("bsa.aspx?cmd=bsa&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getBsas() : Observable<ApiBsaRoot>
}
