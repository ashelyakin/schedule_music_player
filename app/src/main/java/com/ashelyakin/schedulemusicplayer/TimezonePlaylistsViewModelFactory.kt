package com.ashelyakin.schedulemusicplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ashelyakin.schedulemusicplayer.profile.Schedule

class TimezonePlaylistsViewModelFactory(private val schedule: Schedule) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimezonePlaylistsViewModel::class.java)) {
            return TimezonePlaylistsViewModel(schedule) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}