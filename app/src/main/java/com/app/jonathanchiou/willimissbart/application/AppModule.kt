package com.app.jonathanchiou.willimissbart.application

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val appContext: Context) {

    @Provides
    fun provideAppContext() = appContext

    @[Provides Singleton]
    fun provideSharedPreferences(context: Context) : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}