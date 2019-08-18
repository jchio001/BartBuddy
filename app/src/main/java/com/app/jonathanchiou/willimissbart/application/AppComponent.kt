package com.app.jonathanchiou.willimissbart.application

import android.content.Context
import com.app.jonathanchiou.willimissbart.api.BartApiModule
import com.app.jonathanchiou.willimissbart.stations.StationSelectionActivity
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripFragment
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripInfoActivity
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [BartApiModule::class])
@Singleton
interface AppComponent {

    fun inject(tripSelectionFragment: TripSelectionFragment)

    fun inject(realTimeTripFragment: RealTimeTripFragment)
    fun inject(realTimeTripInfoActivity: RealTimeTripInfoActivity)

    fun inject(stationSelectionActivity: StationSelectionActivity)

    companion object : ComponentDelegate<AppComponent>() {

        override val serviceName = "AppComponent"
    }
}

val Context.appComponent by AppComponent