package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_playback.*
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.find
import kotlin.collections.set

class PlayerViewModel(private val activity: Activity, private val player: SimpleExoPlayer, private val schedule: Schedule): ViewModel() {

    private val TAG = "PlayerViewModel"

    var currentPlaylist = MutableLiveData<TimeZonePlaylist>()

    private val playlistsPosition = HashMap<Int, Int>()

    init {
        Log.i(TAG, "init")
        val currentTimezone = TimezoneUtil.getCurrentTimezone(schedule.days)
        if (currentTimezone?.playlists != null) {

            currentPlaylist.postValue(null)
            for (playlist in currentTimezone.playlists)
            {
                playlistsPosition[playlist.playlistID] = -1
            }

            player.addListener(ExoPlayerListener(this))
            addMediaItemsToPlayer()
        }
    }

    private fun addMediaItemsToPlayer() {
        Log.i(TAG, "adding mediaItems to player")

        val currentTimezone = TimezoneUtil.getCurrentTimezone(schedule.days) ?: return

        val currentPlaylistIndex = if (currentPlaylist.value != null)
            currentTimezone.playlists.indexOf(currentPlaylist.value!!)
        else
            -1

        val nextPlaylistIndex = if (currentPlaylistIndex == currentTimezone.playlists.size - 1)
            0
        else
            currentPlaylistIndex + 1

        val nextPlaylist = currentTimezone.playlists[nextPlaylistIndex]
        currentPlaylist.postValue(nextPlaylist)

        val files = SchedulePlaylistsData.getPlaylistsData(nextPlaylist.playlistID)?.files ?: return
        val mediaItemsToAdd = ArrayList<MediaItem>()
        for (i in 1..nextPlaylist.proportion)
        {
            val trackIndex = (playlistsPosition[nextPlaylist.playlistID]!! + 1) % files.size
            playlistsPosition[nextPlaylist.playlistID] = trackIndex
            val mediaItemToAdd = getMediaItem(activity, files[trackIndex])
            mediaItemsToAdd.add(mediaItemToAdd)
        }
        activity.runOnUiThread {
            player.addMediaItems(mediaItemsToAdd)
            player.prepare()
        }

    }

    private fun getMediaItem(context: Context, file: com.ashelyakin.schedulemusicplayer.profile.File): MediaItem {
        return MediaItem.fromUri(
            Uri.fromFile(
                File(
                    context.filesDir.absolutePath,
                    file.id.toString() + ".mp3"
                )
            )
        )
    }

    private var countAddedTracksFromCurrentPlaylist = 0
    fun checkSwitchingPlaylistAndAddTracks() {
        countAddedTracksFromCurrentPlaylist++
        if (countAddedTracksFromCurrentPlaylist == currentPlaylist.value!!.proportion)
        {
            Log.i(TAG, "switching playlist")
            countAddedTracksFromCurrentPlaylist = 0
            addMediaItemsToPlayer()
        }
    }

    //TODO сделать колбэком из активити
    fun fillView(mediaItem: MediaItem?) {
        if (mediaItem == null){
            activity.playlist_name.text = "Нет запланированных плейлистов на текущее время"
            activity.track.text = "Нет текущих треков"
            return
        }
        val schedulePlaylist = SchedulePlaylistsData.getPlaylistsData(currentPlaylist.value!!.playlistID)
        if (schedulePlaylist != null) {
            activity.playlist_name.text = schedulePlaylist.name

            val currentTrackID = getTrackIdFromMediaId(mediaItem.mediaId)
            val currentTrack = schedulePlaylist.files.find { it.id == currentTrackID }
            activity.track.text = currentTrack?.name
        }
    }

    private fun getTrackIdFromMediaId(mediaId: String?): Int {
        return mediaId?.substringBeforeLast('.')?.substringAfterLast('/')?.toInt() ?: -1
    }

}