package com.ashelyakin.schedulemusicplayer.player

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

interface PlayerCallbacks {

    fun play()
    fun pause()
    fun next()
    fun release()
    fun addListener(listener: ExoPlayerListener)
    fun addMediaItems(mediaItems: List<MediaItem>)
    fun prepare()
    fun clearMediaItems()
}