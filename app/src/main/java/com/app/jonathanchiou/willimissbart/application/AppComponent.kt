package com.app.jonathanchiou.willimissbart.application

import com.app.jonathanchiou.willimissbart.stations.StationSelectionActivity
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripFragment
import com.app.jonathanchiou.willimissbart.trips.RealTimeTripInfoActivity
import com.app.jonathanchiou.willimissbart.trips.TripModule
import com.app.jonathanchiou.willimissbart.trips.TripSelectionFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [TripModule::class])
@Singleton
interface AppComponent {

    fun inject(tripSelectionFragment: TripSelectionFragment)

    fun inject(realTimeTripFragment: RealTimeTripFragment)
    fun inject(realTimeTripInfoActivity: RealTimeTripInfoActivity)

    fun inject(stationSelectionActivity: StationSelectionActivity)
}