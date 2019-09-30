package com.app.jonathanchiou.willimissbart.notification

import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg

val RealTimeLeg.timerDuration
    get() = this.duration * 60 - TimerService.ALERT_DURATION
