package com.app.jonathanchiou.willimissbart.timer

import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg

val RealTimeLeg.timerDuration
    get() = this.duration.toLong()
