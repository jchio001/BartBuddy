package com.app.jonathan.willimissbart.timer

import com.app.jonathan.willimissbart.trips.models.internal.RealTimeLeg

val RealTimeLeg.timerDuration
    get() = this.duration * 60 - TimerService.ALERT_DURATION