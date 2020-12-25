package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
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
            val playerViewModelFactory = PlayerViewModelFactory(profile.schedule)
            playerViewModel = ViewModelProvider(activity, playerViewModelFactory)
                    .get(PlayerViewModel::class.java)

            playerViewModel.addMediaItemsToPlayer()
            activity.runOnUiThread {
                player.prepare()
            }

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
            activity.runOnUiThread {
                player.clearMediaItems()
                timer.schedule(PlayTimerTask(), 0)
            }
        }

    }

}