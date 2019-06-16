package com.app.jonathanchiou.willimissbart.api

import com.app.jonathanchiou.willimissbart.BuildConfig
import com.app.jonathanchiou.willimissbart.stations.models.api.StationsRoot
import com.app.jonathanchiou.willimissbart.trips.models.api.DeparturesRoot
import com.app.jonathanchiou.willimissbart.trips.models.api.EtdRoot
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BartService {

    @GET("stn.aspx?cmd=stns&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getStations(): Observable<Response<BartResponseWrapper<StationsRoot>>>

    // Link to the documentation since this endpoint's a mess: https://api.bart.gov/docs/sched/depart.aspx
    @GET("sched.aspx?cmd=depart&date=now&b=0&a=2&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getDepartures(@Query("orig") orig: String,
                      @Query("dest") dest: String): Observable<Response<BartResponseWrapper<DeparturesRoot>>>

    @GET("etd.aspx?cmd=etd&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getRealTimeEstimates(@Query("orig") origin: String): Observable<Response<BartResponseWrapper<EtdRoot>>>
}