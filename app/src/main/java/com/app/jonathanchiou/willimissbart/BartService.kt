package com.app.jonathanchiou.willimissbart

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface BartService {

    @GET("stn.aspx?cmd=stns&json=y&key=${BuildConfig.BART_API_KEY}")
    fun getStations(): Observable<Response<BartResponseWrapper<StationsRoot>>>
}