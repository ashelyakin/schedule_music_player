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

        playerViewModel.fillView(mediaItem)

        if (mediaItem == null)
            return

        playerViewModel.checkSwitchingPlaylistAndAddTracks()
    }

}