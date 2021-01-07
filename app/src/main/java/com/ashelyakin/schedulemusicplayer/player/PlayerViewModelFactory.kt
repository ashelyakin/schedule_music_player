package com.ashelyakin.schedulemusicplayer.player

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.google.android.exoplayer2.SimpleExoPlayer

class PlayerViewModelFactory(private val activity: Activity, private val player: SimpleExoPlayer, private val schedule: Schedule) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(activity, player, schedule) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}