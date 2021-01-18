package com.ashelyakin.schedulemusicplayer

import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist

object SchedulePlaylistsData {

    private val playlistsData = HashMap<Int, SchedulePlaylist>()

    fun getPlaylistsData(id: Int): SchedulePlaylist? {
        return if (playlistsData[id] != null) playlistsData[id]!! else null
    }

    fun getPlaylistsData() = playlistsData

    fun setPlaylistsData(id: Int, schedulePlaylist: SchedulePlaylist){
        playlistsData[id] = schedulePlaylist
    }

}