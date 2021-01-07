package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

class SchedulePlayer(private val activity: Activity, private val schedule: Schedule, private val player: SimpleExoPlayer) {

    private val timer = Timer()

    private lateinit var playerViewModel: PlayerViewModel

    fun start(){
        timer.schedule(PlayTimerTask(), 0)
    }

    //класс задачи по подготовке плеера, в соответствии с текущим временем при ручном запуске
    inner class PlayTimerTask: TimerTask(){
        override fun run() {
            Log.i("SchedulePlayer", "PlayTimerTask running")
            val playerViewModelFactory = PlayerViewModelFactory(activity, player, schedule)
            playerViewModel = ViewModelProvider(activity as ViewModelStoreOwner, playerViewModelFactory)
                    .get(PlayerViewModel::class.java)

            val nextTimeZoneDate = TimezoneUtil.getNextTimezone(schedule.days) ?: return
            scheduleUpdateTimerTask(nextTimeZoneDate)
        }
    }

    private fun scheduleUpdateTimerTask(nextTimeZone: Date) {
        timer.schedule(UpdatePlayTimerTask(), nextTimeZone)
    }

    //класс задачи на обновление плеера
    inner class  UpdatePlayTimerTask : TimerTask(){

        override fun run() {
            Log.i("SchedulePlayer", "UpdatePlayTimerTask running")
            activity.runOnUiThread {
                player.clearMediaItems()
                timer.schedule(PlayTimerTask(), 0)
            }
        }

    }

}