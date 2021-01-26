package com.ashelyakin.schedulemusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*


class ForegroundingService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground()
        else
            startForeground(1, Notification())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "com.ashelyakin.schedulemusicplayer"
        val channelName = "My Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Foregrounding service is running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timer().schedule(StartForegroundedActivityTask(), PlayerApplication.delayMillisForForegrounding)
        return super.onStartCommand(intent, flags, startId)
    }

    inner class StartForegroundedActivityTask: TimerTask() {
        override fun run() {
            if (PlayerApplication.isAppInBackground()) {
                val startForegroundedActivityIntent = Intent(applicationContext, PlayerApplication.foregroundedActivity.javaClass)
                startForegroundedActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                startForegroundedActivityIntent.putExtra(PlayerApplication.extraForIntent.first, PlayerApplication.extraForIntent.second)
                PlayerApplication.foregroundedActivity.startActivity(startForegroundedActivityIntent)
            }
        }

    }

}