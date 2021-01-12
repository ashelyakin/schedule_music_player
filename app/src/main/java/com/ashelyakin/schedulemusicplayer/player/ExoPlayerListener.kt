package com.ashelyakin.schedulemusicplayer.player

import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class ExoPlayerListener(private val playlistManager: PlaylistManager): Player.EventListener{


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Log.i("ExoPlayerListener", "mediaItem was received: ${mediaItem?.mediaId}")

        playlistManager.fillView(mediaItem)

        if (mediaItem == null)
            return

        playlistManager.checkSwitchingPlaylistAndAddTracks()
    }

}