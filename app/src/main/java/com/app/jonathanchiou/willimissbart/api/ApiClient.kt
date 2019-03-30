package com.app.jonathanchiou.willimissbart.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiClient {

    val moshi by lazy {
        Moshi.Builder().build()
    }

    val bartService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.bart.gov/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BartService::class.java)
    }

    companion object {
        val INSTANCE by lazy {
            ApiClient()
        }
    }
}