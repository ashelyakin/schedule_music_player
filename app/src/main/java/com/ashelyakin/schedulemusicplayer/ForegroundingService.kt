package com.ashelyakin.schedulemusicplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.ashelyakin.schedulemusicplayer.activity.MainActivity


class ForegroundingService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        startForeground(42, Notification())
    }

    private fun makeForegroundNotification() {

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = Notification.Builder(this)
        builder.setContentIntent(pendingIntent)
            .setTicker(getString(R.string.app_name))
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher))
        val notification: Notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        startForeground(777, notification)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread(Runnable {
            if (PlayerApplication.isAppInBackground()) {
                val startForegroundedActivityIntent = Intent(applicationContext, PlayerApplication.foregroundedActivity)
                startForegroundedActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                startForegroundedActivityIntent.putExtra(PlayerApplication.extraForIntent.first, PlayerApplication.extraForIntent.second)
                val startForegroundedActivityPendingIntent = PendingIntent.getActivity(applicationContext, 0, startForegroundedActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val am = applicationContext.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+PlayerApplication.delayMillisForForegrounding, startForegroundedActivityPendingIntent)
            }
            stopSelf()
        }).run()
        return super.onStartCommand(intent, flags, startId)
    }

}