package com.app.jonathanchiou.willimissbart.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiClient {

    val moshi by lazy {
        Moshi.Builder()
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