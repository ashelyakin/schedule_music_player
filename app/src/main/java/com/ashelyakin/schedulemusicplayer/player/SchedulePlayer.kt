package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

class SchedulePlayer(private val activity: Activity, private val schedule: Schedule, private val viewModelStore: ViewModelStore) {

    private val TAG = "SchedulePlayer"

    private val timer = Timer()

    private lateinit var player: SimpleExoPlayer

    private lateinit var playerViewModel: PlayerViewModel

    init {
        Log.i(TAG, "starting player")
        timer.schedule(PlayTimerTaskFirstTime(), 0)
    }

    fun play(){
        activity.runOnUiThread {
            player.play()
        }
    }

    fun pause(){
        activity.runOnUiThread {
            player.pause()
        }
    }

    fun next(){
        activity.runOnUiThread {
            player.next()
        }
    }

    fun release(){
        activity.runOnUiThread {
            player.release()
        }
    }

    //класс задачи по включению плеера, в соответствии с текущим временем
    inner class PlayTimerTaskFirstTime() : TimerTask(){
        override fun run() {
            Log.i(TAG, "PlayTimerTaskFirstTime running")
            startPlayer()
        }
    }

    inner class PlayTimerTaskAutomatic() : TimerTask() {
        override fun run() {
            Log.i(TAG, "PlayTimerTaskFirstTime running")
            startPlayer()
            play()
        }
    }

    private fun startPlayer(){
        viewModelStore.clear()

        player = SimpleExoPlayer.Builder(activity).build()
        player.repeatMode = Player.REPEAT_MODE_ALL

        val playerViewModelFactory = PlayerViewModelFactory(activity, player, schedule)
        playerViewModel = ViewModelProvider(activity as ViewModelStoreOwner, playerViewModelFactory)
                .get(PlayerViewModel::class.java)

        //если есть таймзона соответсвующая текущему времени, то создаем задачу на остановку плеера,
        //иначе создаем задачу на обновление плеера по наступлению времени следующей таймзоны
        val nextTimeZoneDate = TimezoneUtil.getNextTimezone(schedule.days) ?: return
        val currentTimeZone = TimezoneUtil.getCurrentTimezone(schedule.days)
        if (currentTimeZone != null){
            val stopTime = TimezoneUtil.getDate(currentTimeZone.to, 0)
            scheduleStopTimerTask(stopTime, nextTimeZoneDate)
        }
        else
            timer.schedule(PlayTimerTaskFirstTime(), nextTimeZoneDate)
    }

    private fun scheduleStopTimerTask(stopTime: Date, nextTimeZoneDate: Date) {
        timer.schedule(StopPlayTimerTask(nextTimeZoneDate), stopTime)
    }
    //класс задачи на остановку плеера и создания таймера на включение плеера в следующей таймзоне
    inner class  StopPlayTimerTask(private val nextTimeZoneDate: Date) : TimerTask(){

        override fun run() {
            Log.i(TAG, "StopPlayTimerTask running")
            activity.runOnUiThread {
                playerViewModel.fillView(null)
                player.release()
            }
            timer.schedule(PlayTimerTaskAutomatic(), nextTimeZoneDate)
        }

    }

}