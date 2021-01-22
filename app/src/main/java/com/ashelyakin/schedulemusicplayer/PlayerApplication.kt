package com.ashelyakin.schedulemusicplayer

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.*


class PlayerApplication(): Application(), LifecycleObserver {

    private var foregroundedActivity: Any? = null

    val ACTION_START_FOREGROUND_ACTIVITY = "com.ashelyakin.schedulemusicplayer.START_FOREGROUND_ACTIVITY"

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.i("PlayerApplication","App backgrounded")

        Thread.sleep(2000)
        /*val startForegroundedActivityIntent = Intent(ACTION_START_FOREGROUND_ACTIVITY)
        startForegroundedActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        sendBroadcast(startForegroundedActivityIntent)*/
        callbacks.start()
        /*val newIntent = Intent()
        newIntent.setClassName("com.ashelyakin.schedulemusicplayer", "com.ashelyakin.schedulemusicplayer.activity.MainActivity")
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(newIntent)*/
        /*val timer = Timer()
        timer.schedule(StartForegroundedActivity(this), 2000)*/

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.i("PlayerApplication","App foregrounded")
    }


    private inner class StartForegroundedActivity(private val context: Context) : TimerTask() {
        override fun run() {
            Log.i("PlayerApplication","Starting foregrounded activity")
            callbacks.start()
            /*val startForegroundedActivityIntent = (if (foregroundedActivity == null) {
                Intent(Intent.ACTION_MAIN)
                //packageManager.getLaunchIntentForPackage("com.ashelyakin.schedulemusicplayer")
            } else {
                Intent(context, foregroundedActivity!!::class.java)
            }) ?: return
            //startForegroundedActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startForegroundedActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            startActivity(startForegroundedActivityIntent)*/
        }

    }

    companion object{

        private lateinit var callbacks: ForegroundCallbacks

        fun setForegroundedActivity(foregroundCallbacks: ForegroundCallbacks){
            //Log.i("PlayerApplication","Foregrounded activity: $activityClass")
            //foregroundedActivity = activityClass
            callbacks = foregroundCallbacks
        }
    }
}