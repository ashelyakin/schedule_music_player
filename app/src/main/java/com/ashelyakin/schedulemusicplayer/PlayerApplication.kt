package com.ashelyakin.schedulemusicplayer

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ashelyakin.schedulemusicplayer.activity.PlaybackActivity
import java.util.*


class PlayerApplication(): Application(), LifecycleObserver {

    private val TAG = "PlayerApplication"

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.i(TAG,"App backgrounded")
        if (isStartForegroundingOn)
            Timer().schedule(StartForegroundedActivity(this), delayMillis)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.i(TAG,"App foregrounded")
    }


    private inner class StartForegroundedActivity(private val context: Context) : TimerTask() {
        override fun run() {
            Log.i(TAG,"Starting foregrounded activity")
            val startForegroundedActivityIntent = Intent(applicationContext, PlaybackActivity::class.java)
            val showOperation = PendingIntent.getActivity(applicationContext, 0, startForegroundedActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmClockInfo = AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 1000, showOperation)
            val am = applicationContext.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            am.setAlarmClock(alarmClockInfo, showOperation)
        }

    }

    companion object{

        private const val defaultDelayMillisForForegrounding: Long = 5000

        private lateinit var foregroundedActivity: Any
        private lateinit var permissionsIntentCallbacks: PermissionsIntentCallbacks
        private var delayMillis = defaultDelayMillisForForegrounding

        private var isStartForegroundingOn = false

        fun startForegrounding(foregroundedActivity: Any, permissionsIntentCallbacks: PermissionsIntentCallbacks, delayMillis: Long = defaultDelayMillisForForegrounding){
            Log.i("PlayerApplication","Starting Foregrounding")
            this.foregroundedActivity = foregroundedActivity
            this.permissionsIntentCallbacks = permissionsIntentCallbacks
            this.delayMillis = delayMillis

            isStartForegroundingOn = true

            permissionsIntentCallbacks.drawOverlays()
            permissionsIntentCallbacks.backgroundStart()

        }
    }
}