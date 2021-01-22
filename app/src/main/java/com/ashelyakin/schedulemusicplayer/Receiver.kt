package com.ashelyakin.schedulemusicplayer

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ashelyakin.schedulemusicplayer.activity.PlaybackActivity
import java.util.*


class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Receiver", "received")
        val startForegroundedActivityIntent = Intent(context, PlaybackActivity::class.java)
        val showOperation = PendingIntent.getActivity(context, 0, startForegroundedActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmClockInfo = AlarmClockInfo(System.currentTimeMillis() + 1000, showOperation)
        val am = context!!.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        am.setAlarmClock(alarmClockInfo, showOperation)
        /*val packageManager = context!!.packageManager
        val intent = packageManager.getLaunchIntentForPackage("com.ashelyakin.schedulemusicplayer")
        if (intent != null) {

            intent.setPackage(null)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            context!!.startActivity(intent)
        }*/
        Log.i("Receiver", "end")
    }
}