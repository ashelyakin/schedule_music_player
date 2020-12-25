package com.ashelyakin.schedulemusicplayer.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ashelyakin.schedulemusicplayer.TimezonePlaylistsViewModel

class PlayerViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}