package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import android.widget.TextView
import com.ashelyakin.schedulemusicplayer.R
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist
import com.ashelyakin.schedulemusicplayer.util.Util
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class ExoPlayerListener(private val activity: Activity, private val player: SimpleExoPlayer, private val schedule: Schedule): Player.EventListener{

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (mediaItem == null)
            return
        fillView(mediaItem)
        val currentPlaylistID = MediaItems.getPlaylistByMediaItem(mediaItem.mediaId)
        if (currentPlaylistID == -1){
            return
        }
        MediaItemUtil.addMediaItemToPlayer(activity, player, schedule, mediaItem)
    }

    private fun fillView(mediaItem: MediaItem?) {
        val currentPlaylistID = MediaItems.getPlaylistByMediaItem(mediaItem!!.mediaId)
        if (currentPlaylistID == -1){
            activity.findViewById<TextView>(R.id.playlist_name).text = "Нет запланированных плейлистов на текущее время"
            activity.findViewById<TextView>(R.id.track).text = "Нет текущих треков"
        }
        else {
            val currentPlaylist = Util.getPlaylistById(schedule.playlists as ArrayList<SchedulePlaylist>, currentPlaylistID)
            activity.findViewById<TextView>(R.id.playlist_name).text = currentPlaylist.name

            val currentTrackID = getTrackIdFromMediaId(mediaItem.mediaId)
            val currentTrack = currentPlaylist.files.find { it.id == currentTrackID}
            activity.findViewById<TextView>(R.id.track).text = currentTrack?.name
        }

    }
}