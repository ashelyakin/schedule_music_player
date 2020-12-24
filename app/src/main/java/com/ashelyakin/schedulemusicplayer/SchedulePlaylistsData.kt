package com.ashelyakin.schedulemusicplayer

import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist

object SchedulePlaylistsData {

    private val playlistsData = HashMap<Int, SchedulePlaylist>()

    fun getPlaylistsData(id: Int): SchedulePlaylist? {
        return if (playlistsData[id] != null) playlistsData[id]!! else null
    }

    fun getPlaylistsData(): List<SchedulePlaylist> {
        val res = ArrayList<SchedulePlaylist>()
        for (playlist in playlistsData)
            res.add(playlist.value)
        return res.toList()
    }

    fun setPlaylistsData(id: Int, schedulePlaylist: SchedulePlaylist){
        playlistsData[id] = schedulePlaylist
    }

}