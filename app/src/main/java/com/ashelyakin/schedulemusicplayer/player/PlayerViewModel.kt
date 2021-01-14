package com.ashelyakin.schedulemusicplayer.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import com.ashelyakin.schedulemusicplayer.activity.ChangeViewTextCallbacks
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

class PlayerViewModel(application: Application): AndroidViewModel(application) {

    private val TAG = "SchedulePlayer"

    private val timer = Timer()

    lateinit var schedule: Schedule
    lateinit var playerCallbacks: PlayerCallbacks
    private lateinit var changeViewTextCallbacks: ChangeViewTextCallbacks

    private lateinit var playlistManager: PlaylistManager
    lateinit var player: SimpleExoPlayer
    var currentPlaylist = MutableLiveData<TimeZonePlaylist>()
    var playlistsPosition = HashMap<Int, Int>()

    fun initPlayer(schedule: Schedule, playerCallbacks: PlayerCallbacks, changeViewTextCallbacks: ChangeViewTextCallbacks){
        Log.i(TAG, "starting player")

        this.schedule = schedule
        this. playerCallbacks = playerCallbacks
        this.changeViewTextCallbacks = changeViewTextCallbacks

        timer.schedule(PlayTimerTaskManual(), 0)
    }

    fun play(){
        playerCallbacks.play()
    }

    fun pause(){
        playerCallbacks.pause()
    }

    fun next(){
        playerCallbacks.next()
    }

    fun release(){
        playerCallbacks.release()
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
        player = SimpleExoPlayer.Builder(getApplication<Application>().applicationContext).build()
        playerCallbacks.addListener(ExoPlayerListener(this))
        playlistManager = PlaylistManager(this, getApplication<Application>().applicationContext)
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
            fillView(null)
            playerCallbacks.release()
            timer.schedule(PlayTimerTaskByTime(), nextTimeZoneDate)
        }
    }

    private var countAddedTracksFromCurrentPlaylist = 0
    fun checkSwitchingPlaylistAndAddTracks() {
        countAddedTracksFromCurrentPlaylist++
        if (countAddedTracksFromCurrentPlaylist == currentPlaylist.value!!.proportion)
        {
            Log.i(TAG, "switching playlist")
            countAddedTracksFromCurrentPlaylist = 0
            playlistManager.addMediaItemsToPlaylist()
        }
    }

    fun fillView(mediaItem: MediaItem?) {
        if (mediaItem == null){
            changeViewTextCallbacks.changePlaylistName(null)
            changeViewTextCallbacks.changeTrackName(null)
        }
        else {
            val schedulePlaylist = SchedulePlaylistsData.getPlaylistsData(currentPlaylist.value!!.playlistID)
            if (schedulePlaylist != null) {
                changeViewTextCallbacks.changePlaylistName(schedulePlaylist.name)

                val currentTrackID = getTrackIdFromMediaId(mediaItem.mediaId)
                val currentTrack = schedulePlaylist.files.find { it.id == currentTrackID }
                changeViewTextCallbacks.changeTrackName(currentTrack?.name)
            }
        }
    }

    private fun getTrackIdFromMediaId(mediaId: String?): Int {
        return mediaId?.substringBeforeLast('.')?.substringAfterLast('/')?.toInt() ?: -1
    }

}