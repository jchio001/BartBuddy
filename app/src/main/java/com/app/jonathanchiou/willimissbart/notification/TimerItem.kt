package com.app.jonathanchiou.willimissbart.notification

sealed class TimerItem(
    val title: String,
    val initialDuration: Long
) {

    class Timer(
        title: String,
        initialDuration: Long
    ) : TimerItem(title, initialDuration)

    class Alert(title: String
    ) : TimerItem(title, TimerService.ALERT_DURATION)
}
