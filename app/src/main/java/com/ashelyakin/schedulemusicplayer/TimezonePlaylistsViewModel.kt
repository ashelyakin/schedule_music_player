package com.ashelyakin.schedulemusicplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist

class TimezonePlaylistsViewModel(schedule: Schedule): ViewModel() {

    val timezonePlaylistsData = MutableLiveData<HashMap<Int, TimeZonePlaylist>>()

    init {
        timezonePlaylistsData.value = HashMap()
        for (timezonePlaylist in schedule.days.flatMap { it.timeZones.flatMap { it.playlists } })
        {
            timezonePlaylistsData.value?.set(timezonePlaylist.playlistID, timezonePlaylist)
        }
    }

    fun plusProportion(id: Int){
        setProportion(id, ProportionBtnClickType.PLUS)
    }

    fun minusProportion(id: Int){
        setProportion(id, ProportionBtnClickType.MINUS)
    }

    private enum class ProportionBtnClickType {
        PLUS, MINUS
    }

    private fun setProportion(id: Int, type: ProportionBtnClickType ){
        //timezonePlaylistsData.value?.get(id)?.proportion = timezonePlaylistsData.value?.get(id)?.proportion?.plus(1)!!
        val timezonePlaylist = timezonePlaylistsData.value?.get(id)
        val currentProportion = timezonePlaylist?.proportion!!
        if (type == ProportionBtnClickType.PLUS)
            timezonePlaylist.proportion = currentProportion + 1
        else if (currentProportion > 1)
            timezonePlaylist.proportion = currentProportion - 1
        val newTimezonePlaylistsData = HashMap(timezonePlaylistsData.value)
        newTimezonePlaylistsData[id] = timezonePlaylist
        timezonePlaylistsData.postValue(newTimezonePlaylistsData)
        //timezonePlaylistsData.value?.set(id, timezonePlaylist)
    }
}