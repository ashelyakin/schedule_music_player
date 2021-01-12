package com.ashelyakin.schedulemusicplayer.player

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import com.ashelyakin.schedulemusicplayer.activity.ChangeViewTextCallbacks
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.google.android.exoplayer2.MediaItem
import java.io.File

class PlaylistManager(private val playerViewModel: PlayerViewModel, private val schedule: Schedule,
                      private val playerCallbacks: PlayerCallbacks,
                      private val changeViewTextCallbacks: ChangeViewTextCallbacks, private val context: Context){

    private val TAG = "PlayerViewModel"

    init {
        Log.i(TAG, "init")
        val currentTimezone = TimezoneUtil.getCurrentTimezone(schedule.days)
        if (currentTimezone?.playlists != null) {

            playerViewModel.currentPlaylist.postValue(null)
            for (playlist in currentTimezone.playlists)
            {
                playerViewModel.playlistsPosition[playlist.playlistID] = -1
            }

            playerCallbacks.addListener(playerViewModel.player, ExoPlayerListener(this))
            addMediaItemsToPlaylist()
        }
    }

    private fun addMediaItemsToPlaylist() {
        Log.i(TAG, "adding mediaItems to player")

        val currentTimezone = TimezoneUtil.getCurrentTimezone(schedule.days) ?: return

        val indexOfNextPlaylist = getIndexOfNextPlaylist(currentTimezone)

        val nextPlaylist = currentTimezone.playlists[indexOfNextPlaylist]
        playerViewModel.currentPlaylist.postValue(nextPlaylist)

        val files = SchedulePlaylistsData.getPlaylistsData(nextPlaylist.playlistID)?.files ?: return
        val mediaItemsToAdd = ArrayList<MediaItem>()
        for (i in 1..nextPlaylist.proportion)
        {
            val trackIndex = (playerViewModel.playlistsPosition[nextPlaylist.playlistID]!! + 1) % files.size
            playerViewModel.playlistsPosition[nextPlaylist.playlistID] = trackIndex
            val mediaItemToAdd = getMediaItem(context, files[trackIndex])
            mediaItemsToAdd.add(mediaItemToAdd)
        }

        playerCallbacks.addMediaItems(playerViewModel.player, mediaItemsToAdd)
        playerCallbacks.prepare(playerViewModel.player)

    }

    private fun getIndexOfNextPlaylist(currentTimezone: TimeZone): Int{
        val indexOfCurrentPlaylist = getIndexOfCurrentPlaylist(currentTimezone)
        return if (indexOfCurrentPlaylist == currentTimezone.playlists.size - 1)
            0
        else
            indexOfCurrentPlaylist + 1
    }

    private fun getIndexOfCurrentPlaylist(currentTimezone: TimeZone): Int{
        return if (playerViewModel.currentPlaylist.value != null)
            currentTimezone.playlists.indexOf(playerViewModel.currentPlaylist.value!!)
        else
            -1
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
        if (countAddedTracksFromCurrentPlaylist == playerViewModel.currentPlaylist.value!!.proportion)
        {
            Log.i(TAG, "switching playlist")
            countAddedTracksFromCurrentPlaylist = 0
            addMediaItemsToPlaylist()
        }
    }

    fun fillView(mediaItem: MediaItem?) {
        if (mediaItem == null){
            changeViewTextCallbacks.changePlaylistName(null)
            changeViewTextCallbacks.changeTrackName(null)
        }
        else {
            val schedulePlaylist = SchedulePlaylistsData.getPlaylistsData(playerViewModel.currentPlaylist.value!!.playlistID)
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