package com.app.jonathan.willimissbart.application

import android.content.Context
import com.app.jonathan.willimissbart.TripActivity
import com.app.jonathan.willimissbart.api.BartApiModule
import com.app.jonathan.willimissbart.bsa.BsasFragment
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTripsFragment
import com.app.jonathan.willimissbart.stations.StationSelectionActivity
import com.app.jonathan.willimissbart.timer.TimerService
import com.app.jonathan.willimissbart.trips.TripSelectionFragment
import com.app.jonathanchiou.willimissbart.db.DatabaseModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, BartApiModule::class, DatabaseModule::class])
@Singleton
interface AppComponent {

    fun inject(tripActivity: TripActivity)

    fun inject(tripSelectionFragment: TripSelectionFragment)

    fun inject(realTimeTripsFragment: RealTimeTripsFragment)

    fun inject(stationSelectionActivity: StationSelectionActivity)

    fun inject(realTimeTripNotificationService: TimerService)

    fun inject(bsasFragment: BsasFragment)

    companion object : ComponentDelegate<AppComponent>() {

        override val serviceName = "AppComponent"
    }
}

val Context.appComponent by AppComponent
