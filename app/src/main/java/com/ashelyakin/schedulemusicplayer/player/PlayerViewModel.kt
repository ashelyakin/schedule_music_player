package com.ashelyakin.schedulemusicplayer.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ashelyakin.schedulemusicplayer.activity.ChangeViewTextCallbacks
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

class PlayerViewModel(private val schedule: Schedule, private val playerCallbacks: PlayerCallbacks, private val viewModelStore: ViewModelStore,
                      private val changeViewTextCallbacks: ChangeViewTextCallbacks, application: Application): AndroidViewModel(application) {

    private val TAG = "SchedulePlayer"

    private val timer = Timer()

    lateinit var player: SimpleExoPlayer

    private lateinit var playlistManager: PlaylistManager

    var currentPlaylist = MutableLiveData<TimeZonePlaylist>()

    var playlistsPosition = HashMap<Int, Int>()

    init {
        Log.i(TAG, "starting player")
        timer.schedule(PlayTimerTaskManual(), 0)
    }

    fun play(){
        playerCallbacks.play(player)
    }

    fun pause(){
        playerCallbacks.pause(player)
    }

    fun next(){
        playerCallbacks.next(player)
    }

    fun release(){
        playerCallbacks.release(player)
    }

    private var btnPlayWasNotPressed = true
    fun changePlayerState(isBtnPlayNow: Boolean){
        if (isBtnPlayNow) {

            if (btnPlayWasNotPressed) {
                player.play()
                btnPlayWasNotPressed = false
            }
            else{
                player.next()
                player.play()
            }
        }
        else {
            player.pause()
        }
    }

    //класс задачи по включению плеера, в соответствии с текущим временем
    inner class PlayTimerTaskManual() : TimerTask(){
        override fun run() {
            Log.i(TAG, "PlayTimerTaskManual running")
            startPlayer()
        }
    }

    inner class PlayTimerTaskByTime() : TimerTask() {
        override fun run() {
            Log.i(TAG, "PlayTimerTaskByTime running")
            startPlayer()
            play()
        }
    }

    private fun startPlayer(){
        viewModelStore.clear()

        player = SimpleExoPlayer.Builder(getApplication<Application>().applicationContext).build()

        playlistManager = PlaylistManager(this, schedule, playerCallbacks, changeViewTextCallbacks, getApplication<Application>().applicationContext)

        //если есть таймзона соответсвующая текущему времени, то создаем задачу на остановку плеера,
        //иначе создаем задачу на обновление плеера по наступлению времени следующей таймзоны
        val nextTimeZoneDate = TimezoneUtil.getNextTimezone(schedule.days) ?: return
        val currentTimeZone = TimezoneUtil.getCurrentTimezone(schedule.days)
        if (currentTimeZone != null){
            val stopTime = TimezoneUtil.getDate(currentTimeZone.to, 0)
            scheduleStopTimerTask(stopTime, nextTimeZoneDate)
        }
        else
            timer.schedule(PlayTimerTaskManual(), nextTimeZoneDate)
    }

    private fun scheduleStopTimerTask(stopTime: Date, nextTimeZoneDate: Date) {
        timer.schedule(StopPlayTimerTask(nextTimeZoneDate), stopTime)
    }
    //класс задачи на остановку плеера и создания таймера на включение плеера в следующей таймзоне
    inner class  StopPlayTimerTask(private val nextTimeZoneDate: Date) : TimerTask(){

        override fun run() {
            Log.i(TAG, "StopPlayTimerTask running")
            playlistManager.fillView(null)
            playerCallbacks.release(player)
            timer.schedule(PlayTimerTaskByTime(), nextTimeZoneDate)
        }

    }

}