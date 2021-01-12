package com.ashelyakin.schedulemusicplayer.player

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

interface PlayerCallbacks {

    fun play(player: SimpleExoPlayer)
    fun pause(player: SimpleExoPlayer)
    fun next(player: SimpleExoPlayer)
    fun release(player: SimpleExoPlayer)
    fun addListener(player: SimpleExoPlayer, listener: ExoPlayerListener)
    fun addMediaItems(player: SimpleExoPlayer, mediaItems: List<MediaItem>)
    fun prepare(player: SimpleExoPlayer)
}