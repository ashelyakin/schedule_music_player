package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

class SchedulePlayer(private val activity: Activity, private val schedule: Schedule, private val player: SimpleExoPlayer) {

    private val TAG = "SchedulePlayer"

    private val timer = Timer()

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var viewModelStore: ViewModelStore

    fun start(viewModelStore: ViewModelStore) {
        this.viewModelStore = viewModelStore
        timer.schedule(PlayTimerTask(), 0)
    }
    //класс задачи по подготовке плеера, в соответствии с текущим временем при ручном запуске
    inner class PlayTimerTask() : TimerTask(){
        override fun run() {
            Log.i(TAG, "PlayTimerTask running")
            viewModelStore.clear()
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
                scheduleUpdatePlayerTimerTask(nextTimeZoneDate)

        }
    }


    private fun scheduleStopTimerTask(stopTime: Date, nextTimeZoneDate: Date) {
        timer.schedule(StopPlayTimerTask(nextTimeZoneDate), stopTime)
    }
    //класс задачи на остановку плеера и создания таймера на обновления плеера для следующей таймзоны
    inner class  StopPlayTimerTask(private val nextTimeZoneDate: Date) : TimerTask(){

        override fun run() {
            Log.i(TAG, "UpdatePlayTimerTask running")
            activity.runOnUiThread {
                player.pause()
                playerViewModel.fillView(null)
                player.clearMediaItems()
            }
            scheduleUpdatePlayerTimerTask(nextTimeZoneDate)
        }

    }


    private fun scheduleUpdatePlayerTimerTask(nextTimeZone: Date) {
        timer.schedule(UpdatePlayTimerTask(), nextTimeZone)
    }
    //класс задачи на обновление плеера
    inner class  UpdatePlayTimerTask : TimerTask(){

        override fun run() {
            Log.i(TAG, "UpdatePlayTimerTask running")
            timer.schedule(PlayTimerTask(), 0)
            activity.runOnUiThread {
                player.play()
            }
        }

    }

}