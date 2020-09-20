package com.app.jonathan.willimissbart.api

import com.app.jonathan.willimissbart.moshi.BartIntegerAdapter
import com.app.jonathan.willimissbart.moshi.DateAdapter
import com.app.jonathan.willimissbart.retrofit.BartResponseConverterFactory
import com.app.jonathanchiou.willimissbart.api.models.SingleToListFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object BartApiModule {

    @[JvmStatic Provides Singleton]
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(BartIntegerAdapter())
            .add(DateAdapter())
            .add(SingleToListFactory())
            .build()
    }

    @[JvmStatic Provides Singleton]
    fun provideBartService(moshi: Moshi): BartService {
        return Retrofit.Builder()
            .baseUrl("https://api.bart.gov/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(BartResponseConverterFactory())
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
}
