package com.app.jonathanchiou.willimissbart.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.notification.TimerService.Companion.toTimerText
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeLeg
import com.app.jonathanchiou.willimissbart.trips.models.internal.RealTimeTrip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TimerService : Service() {

    private val timerSubject = PublishSubject.create<RealTimeTrip>()
    private val compositeDisposable = CompositeDisposable()

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
                .switchMap { realTimeTrip ->
                    Observable.fromIterable(realTimeTrip.realTimeLegs)
                        .concatMap { realTimeLeg ->
                            val durationAsSeconds = (realTimeLeg.duration * 60).toLong()

                            Observable.intervalRange(1, durationAsSeconds + 1, 0, 1, TimeUnit.SECONDS)
                                .map { durationAsSeconds - it }
                                .map { realTimeLeg to it }
                                .observeOn(AndroidSchedulers.mainThread())
                        }
                }
                .subscribe { (realTimeLeg, duration) ->
                    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .addAction(
                            R.drawable.ic_train_icon,
                            this.getString(R.string.cancel),
                            dismissIntent
                        )
                        .setContentTitle(duration.toTimerText())
                        .setContentText(realTimeLeg.asTitle())
                        .setSmallIcon(R.drawable.ic_train_icon)
                        .build()

                    startForeground(NOTIFICATION_ID, notification)
                }
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW -> {
                val notificationManager = getNotificationManager()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && notificationManager.getNotificationChannel(CHANNEL_ID) == null
                ) {
                    notificationManager.createNotificationChannel(
                        NotificationChannel(
                            CHANNEL_ID,
                            "BartBuddy real time trip timer",
                            NotificationManager.IMPORTANCE_LOW
                        )
                            .apply {
                                setShowBadge(false)
                            }
                    )
                }

                val realTimeTrip = intent.getParcelableExtra<RealTimeTrip>(REAL_TIME_TRIP_ARG)
                timerSubject.onNext(realTimeTrip)
            }
            ACTION_DISMISS -> {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIFICATION_ID)
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

        const val CHANNEL_ID = "will_I_miss_bart_real_time_trip_channel"

        const val NOTIFICATION_ID = 73

        const val ACTION_SHOW = "BartBuddy.show"
        const val ACTION_DISMISS = "BartBuddy.dismiss"

        const val ELAPSED_SECONDS = "elapsed_seconds"
        const val REAL_TIME_TRIP_ARG = "duration"

        private fun Long.toTimerText(): String {
            val minutes = this / 60
            val seconds = this % 60

            return "${if (minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
        }

        private fun RealTimeLeg.asTitle() = when (this) {
            is RealTimeLeg.Train -> "${this.origin} -> ${this.destination}"
            is RealTimeLeg.Wait -> "Until the next train heading towards ${this.nextTrainHeadStation}"
        }

        private fun Context.getNotificationManager() =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
