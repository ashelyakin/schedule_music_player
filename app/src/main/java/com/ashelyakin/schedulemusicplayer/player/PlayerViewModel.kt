package com.ashelyakin.schedulemusicplayer.player

import android.media.browse.MediaBrowser
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.google.android.exoplayer2.MediaItem

class PlayerViewModel(): ViewModel() {

    var currentPlaylist = MutableLiveData<TimeZonePlaylist>()

    var lastAddedMediaItemFromCurrentPlaylist = MutableLiveData<MediaItem>()

    init {
        addMediaItemsToPlayer()
    }

}