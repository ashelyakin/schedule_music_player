package com.ashelyakin.schedulemusicplayer.player

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class ExoPlayerListener(private val playerViewModel: PlayerViewModel): Player.EventListener{


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        Log.i("ExoPlayerListener", "mediaItem was received: ${mediaItem?.mediaId}")
        if (mediaItem == null)
            return

        playerViewModel.fillView(mediaItem)
        playerViewModel.checkSwitchingPlaylistAndAddTracks()
    }

}