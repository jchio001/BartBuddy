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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class TimerService : Service() {

    private val timerSubject = PublishSubject.create<Long>()
    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable.add(
            timerSubject
                .switchMap { value ->
                    Observable.intervalRange(1, value + 1, 0, 1, TimeUnit.SECONDS)
                        .map { value + 1 - it }
                        .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe { time ->
                    Log.d("Timer", time.toString())

                    val notificationManager = getNotificationManager()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                        notificationManager.createNotificationChannel(
                            NotificationChannel(
                                CHANNEL_ID,
                                "BartBuddy real time trip timer",
                                NotificationManager.IMPORTANCE_HIGH
                            )
                        )
                    }

                    notificationManager.notify(
                        NOTIFICATION_ID,
                        NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_train_icon)
                            .setContentTitle("pizza")
                            .setContentText(time.toTimerText())
                            .setOngoing(true)
                            .addAction(
                                R.drawable.ic_train_icon,
                                this.getString(R.string.cancel),
                                PendingIntent.getService(
                                    this,
                                    NOTIFICATION_ID,
                                    Intent(this, TimerService::class.java)
                                        .setAction(ACTION_DISMISS),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                            .build()
                    )
                }
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_SHOW -> {
                val duration = intent.getLongExtra(DURATION_ARG, -1)
                if (duration > -1) {
                    timerSubject.onNext(duration)
                }
            }
            ACTION_DISMISS -> {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIFICATION_ID)
                this.stopService(intent)
            }
        }

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        compositeDisposable.clear()
        return super.stopService(name)
    }

    companion object {

        const val CHANNEL_ID = "bart-buddy-channel"

        const val NOTIFICATION_ID = 73

        const val ACTION_SHOW = "BartBuddy.show"
        const val ACTION_DISMISS = "BartBuddy.dismiss"

        const val DURATION_ARG = "duration"

        private fun Long.toTimerText() : String {
            val minutes = this / 60
            val seconds = this % 60

            return "${if (minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
        }
        private fun Context.getNotificationManager() =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fun Context.startRealTimeTripTimer(minutes: Long) {
            this.startService(
                Intent(this, TimerService::class.java)
                    .setAction(ACTION_SHOW)
                    .putExtra(DURATION_ARG, minutes * 60)
            )
        }
    }
}