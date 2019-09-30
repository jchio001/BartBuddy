package com.app.jonathanchiou.willimissbart.timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.application.appComponent
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimerService : Service() {

    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var vibrator: Vibrator

    private val timerSubject = PublishSubject.create<TimerItem>()
    private val compositeDisposable = CompositeDisposable()

    private var currentTimerItems = listOf<TimerItem>()
    private var currentIndex = Integer.MIN_VALUE

    private val dismissIntent by lazy {
        PendingIntent.getService(
            this,
            0,
            Intent(this, TimerService::class.java)
                .setAction(ACTION_DISMISS),
            PendingIntent.FLAG_ONE_SHOT)
    }

    init {
        compositeDisposable.add(
            timerSubject
                .doOnNext { timerItem ->
                    when (timerItem) {
                        is TimerItem.Alert -> vibrator.vibrate(vibrationPattern, 0)
                        is TimerItem.Timer -> vibrator.cancel()
                        is TimerItem.Terminal -> {
                            vibrator.cancel()
                            stopForeground(true)
                            // Can't remove a notification if it's bound to the foreground!
                            notificationManager.cancel(TIMER_NOTIFICATION_ID)
                        }
                    }
                }
                .switchMap { timerItem ->
                    if (timerItem !is TimerItem.Terminal) {
                        Observable.intervalRange(
                            0,
                            timerItem.initialDuration + 2,
                            0,
                            1,
                            TimeUnit.SECONDS
                        )
                            .map { timerItem to timerItem.initialDuration - it }
                            .observeOn(AndroidSchedulers.mainThread())
                    } else {
                        Observable.never()
                    }
                }
                .subscribe { (timerItem, duration) ->
                    if (duration >= 0L) {
                        val notification = NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
                            .addAction(
                                R.drawable.ic_train_icon,
                                this.getString(R.string.cancel),
                                dismissIntent
                            )
                            .setContentTitle(duration.toTimerText())
                            .setContentText(timerItem.title)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.ic_train_icon)
                            .build()

                        startForeground(TIMER_NOTIFICATION_ID, notification)
                    } else {
                        if (++currentIndex < currentTimerItems.size) {
                            timerSubject.onNext(currentTimerItems[currentIndex])
                        }
                    }
                }
        )
    }

    override fun onCreate() = appComponent.inject(this)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notificationManager.getNotificationChannel(TIMER_CHANNEL_ID) == null) {
                        notificationManager.createNotificationChannel(
                            NotificationChannel(
                                TIMER_CHANNEL_ID,
                                "BartBuddy timer",
                                NotificationManager.IMPORTANCE_LOW
                            )
                                .apply {
                                    setShowBadge(false)
                                }
                        )
                    }
                }

                val realTimeTrip = intent.getParcelableExtra<RealTimeTrip>(REAL_TIME_TRIP_ARG)
                currentTimerItems = realTimeTrip.realTimeLegs.toTimerItems()
                currentIndex = 0
                timerSubject.onNext(currentTimerItems[currentIndex])
            }
            ACTION_NEXT_ITEM -> timerSubject.onNext(currentTimerItems[++currentIndex])
            ACTION_DISMISS -> {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(TIMER_NOTIFICATION_ID)
                stopService(intent)
            }
        }

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        stopForeground(true)
        compositeDisposable.clear()
        return super.stopService(name)
    }

    companion object {

        const val TIMER_CHANNEL_ID = "will_I_miss_bart_real_time_trip_channel"

        const val TIMER_NOTIFICATION_ID = 69

        const val ACTION_SHOW = "BartBuddy.show"
        const val ACTION_NEXT_ITEM = "BartBuddy.next_item"
        const val ACTION_DISMISS = "BartBuddy.dismiss"

        const val ELAPSED_SECONDS = "elapsed_seconds"
        const val REAL_TIME_TRIP_ARG = "duration"

        const val ALERT_DURATION = 15L

        val vibrationPattern = LongArray(3).also {
            it[1] = 1000L
            it[2] = 1000L
        }

        private fun Long.toTimerText(): String {
            val minutes = this / 60
            val seconds = this % 60

            return "${if (minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
        }

        private fun RealTimeLeg.asTimerTitle() = when (this) {
            is RealTimeLeg.Train -> "Until exiting at ${this.destination}"
            is RealTimeLeg.Wait -> "Until the next train heading towards ${this.nextTrainHeadStation}"
        }

        private fun RealTimeLeg.asAlertTitle() = when (this) {
            is RealTimeLeg.Train -> "Prepare to exit at ${this.destination} now!"
            is RealTimeLeg.Wait -> "Expect the ${this.nextTrainHeadStation} train soon!"
        }

        private fun List<RealTimeLeg>.toTimerItems(): List<TimerItem> {
            val timerItems = ArrayList<TimerItem>(this.size * 2 + 1)

            for (realTimeLeg in this) {
                val duration = realTimeLeg.duration
                if (duration > 0) {
                    timerItems.add(
                        TimerItem.Timer(
                            title = realTimeLeg.asTimerTitle(),
                            initialDuration = realTimeLeg.timerDuration
                        )
                    )
                }
                timerItems.add(
                    TimerItem.Alert(title = realTimeLeg.asAlertTitle())
                )
            }

            timerItems.add(TimerItem.Terminal)

            return timerItems
        }

        fun Context.startRealTimeTripTimer(realTimeTrip: RealTimeTrip) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(
                    Intent(this, TimerService::class.java)
                        .setAction(ACTION_SHOW)
                        .putExtra(ELAPSED_SECONDS, 0)
                        .putExtra(REAL_TIME_TRIP_ARG, realTimeTrip)
                )
            } else {
                this.startService(
                    Intent(this, TimerService::class.java)
                        .setAction(ACTION_SHOW)
                        .putExtra(ELAPSED_SECONDS, 0)
                        .putExtra(REAL_TIME_TRIP_ARG, realTimeTrip)
                )
            }
        }
    }
}
