package com.ashelyakin.schedulemusicplayer

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.*


class PlayerApplication(): Application(), LifecycleObserver {

    private val TAG = "PlayerApplication"

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksManager())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.i(TAG,"App backgrounded")
        if (isStartForegroundingOn)
            startService(Intent(this, ForegroundingService::class.java))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.i(TAG,"App foregrounded")
    }

    companion object{

        private lateinit var context: Context

        private const val defaultDelayMillisForForegrounding: Long = 5000

        lateinit var foregroundedActivity: Class<*>
        private lateinit var permissionsIntentCallbacks: PermissionsIntentCallbacks
        var delayMillisForForegrounding = defaultDelayMillisForForegrounding
        lateinit var extraForIntent: Pair<String, Parcelable>

        var isStartForegroundingOn = false

        private var countStartedActivities = 0

        fun isAppInBackground() = countStartedActivities == 0

        fun <T : Activity> startForegrounding(foregroundedActivity: Class<T>, permissionsIntentCallbacks: PermissionsIntentCallbacks, extraForIntent: Pair<String, Parcelable>, delayMillis: Long = defaultDelayMillisForForegrounding){
            Log.i("PlayerApplication","Starting foregrounding")
            this.foregroundedActivity = foregroundedActivity
            this.permissionsIntentCallbacks = permissionsIntentCallbacks
            this.delayMillisForForegrounding = delayMillis
            this.extraForIntent = extraForIntent

            isStartForegroundingOn = true

            permissionsIntentCallbacks.drawOverlays()
            permissionsIntentCallbacks.backgroundStart()

        }

        fun stopForegrounding(){
            Log.i("PlayerApplication","Stop foregrounding")
            isStartForegroundingOn = false
        }
    }

    inner class ActivityLifecycleCallbacksManager: ActivityLifecycleCallbacks {

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStarted(activity: Activity) {
            ++countStartedActivities
        }

        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityStopped(activity: Activity) {
            --countStartedActivities
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityResumed(activity: Activity) {}

    }

}