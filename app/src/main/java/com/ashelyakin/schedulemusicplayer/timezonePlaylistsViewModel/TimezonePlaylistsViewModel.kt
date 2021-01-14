package com.ashelyakin.schedulemusicplayer.timezonePlaylistsViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.recyclerView.ItemType
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil

class TimezonePlaylistsViewModel(schedule: Schedule): ViewModel() {

    //список элементов для recyclerview, состоящий из списоков дней, таймзон и плейлистов, преобразованных в одномерную форму
    val recyclerItems = ArrayList<Pair<Any, ItemType>>()

    //позиция последнего плейлиста, в котором изменились пропорции
    val changedPlaylistPosition = MutableLiveData<Int>()

    init {
        for (day in schedule.days){
            recyclerItems.add(Pair(day, ItemType.DAY))
            //сортировка списка timeZones
            val sortTimeZones = TimezoneUtil.sortTimeZones(day.timeZones as ArrayList<TimeZone>)

            for (timeZone in sortTimeZones){
                recyclerItems.add(Pair(timeZone, ItemType.TIMEZONE))
                for (playlist in timeZone.playlists){
                    recyclerItems.add(Pair(playlist, ItemType.PLAYLIST))
                }
            }
        }
    }

    fun plusProportion(position: Int){
        setProportion(position, ProportionBtnClickType.PLUS)
    }

    fun minusProportion(position: Int){
        setProportion(position, ProportionBtnClickType.MINUS)
    }

    private enum class ProportionBtnClickType {
        PLUS, MINUS
    }

    private fun setProportion(position: Int, type: ProportionBtnClickType){
        val timezonePlaylist = recyclerItems[position].first as TimeZonePlaylist
        if (type == ProportionBtnClickType.PLUS)
            timezonePlaylist.proportion += 1
        else if (timezonePlaylist.proportion > 1)
            timezonePlaylist.proportion -= 1

        changedPlaylistPosition.postValue(position)
    }
}