package com.app.jonathan.willimissbart.application

import android.content.Context
import com.app.jonathan.willimissbart.TripActivity
import com.app.jonathan.willimissbart.api.BartApiModule
import com.app.jonathan.willimissbart.stations.StationSelectionActivity
import com.app.jonathan.willimissbart.timer.TimerService
import com.app.jonathan.willimissbart.trips.RealTimeTripFragment
import com.app.jonathan.willimissbart.trips.TripSelectionFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, BartApiModule::class])
@Singleton
interface AppComponent {

    fun inject(tripActivity: TripActivity)

    fun inject(tripSelectionFragment: TripSelectionFragment)

    fun inject(realTimeTripFragment: RealTimeTripFragment)

    fun inject(stationSelectionActivity: StationSelectionActivity)

    fun inject(realTimeTripNotificationService: TimerService)

    companion object : ComponentDelegate<AppComponent>() {

        override val serviceName = "AppComponent"
    }
}

val Context.appComponent by AppComponent
