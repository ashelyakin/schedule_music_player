package com.ashelyakin.schedulemusicplayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity

class ForegroundingService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (PlayerApplication.isStartForegroundingOn && PlayerApplication.isAppInBackground()) {
            val startForegroundedActivityIntent = Intent(applicationContext, PlayerApplication.foregroundedActivity)
            startForegroundedActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            startForegroundedActivityIntent.putExtra(PlayerApplication.extraForIntent.first, PlayerApplication.extraForIntent.second)
            val startForegroundedActivityPendingIntent = PendingIntent.getActivity(applicationContext, 0, startForegroundedActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = applicationContext.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PlayerApplication.delayMillisForForegrounding, startForegroundedActivityPendingIntent)
        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

}