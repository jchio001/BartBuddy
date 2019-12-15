package com.app.jonathan.willimissbart.application

import android.app.NotificationManager
import android.content.Context
import android.os.Vibrator
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val appContext: Context) {

    @Provides
    fun providesContext() = appContext

    @[Provides Singleton]
    fun providesNotificationManager(context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @[Provides Singleton]
    fun provideSharedPreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)

    @[Provides Singleton]
    fun providesVibrator(context: Context) = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}
