package com.ashelyakin.schedulemusicplayer.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.ashelyakin.schedulemusicplayer.activity.ChangeViewTextCallbacks
import com.ashelyakin.schedulemusicplayer.profile.Schedule

class PlayerViewModelFactory(private val schedule: Schedule, private val playerCallbacks: PlayerCallbacks, private val store: ViewModelStore,
                             private val changeViewTextCallbacks: ChangeViewTextCallbacks,
                             private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(schedule, playerCallbacks, store,  changeViewTextCallbacks, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}