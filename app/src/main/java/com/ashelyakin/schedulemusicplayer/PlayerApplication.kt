package com.ashelyakin.schedulemusicplayer

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log


class PlayerApplication(): Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksManager())
    }

    companion object{

        private const val defaultDelayMillisForForegrounding: Long = 3000

        lateinit var foregroundedActivity: Activity
        private lateinit var permissionsIntentCallbacks: PermissionsIntentCallbacks
        var delayMillisForForegrounding = defaultDelayMillisForForegrounding
        lateinit var extraForIntent: Pair<String, Parcelable>

        private var countStartedActivities = 0
        fun isAppInBackground() = countStartedActivities == 0

        private var isStartForegroundingOn = false

        fun initForegrounding(foregroundedActivity: Activity, permissionsIntentCallbacks: PermissionsIntentCallbacks, extraForIntent: Pair<String, Parcelable>, delayMillis: Long = defaultDelayMillisForForegrounding){
            this.foregroundedActivity = foregroundedActivity
            this.permissionsIntentCallbacks = permissionsIntentCallbacks
            this.delayMillisForForegrounding = delayMillis
            this.extraForIntent = extraForIntent

            Companion.permissionsIntentCallbacks.drawOverlays()
            Companion.permissionsIntentCallbacks.backgroundStart()
        }

        fun startForegrounding(){
            Log.i("PlayerApplication","Starting foregrounding")
            isStartForegroundingOn = true
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
            if (isStartForegroundingOn && activity == foregroundedActivity)
                stopService(Intent(baseContext, ForegroundingService::class.java))
        }

        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityStopped(activity: Activity) {
            --countStartedActivities
            if (isStartForegroundingOn &&  activity == foregroundedActivity)
                startService(Intent(baseContext, ForegroundingService::class.java))
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityResumed(activity: Activity) {}

    }

}