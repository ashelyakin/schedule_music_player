package com.ashelyakin.schedulemusicplayer.util

import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist
import com.ashelyakin.schedulemusicplayer.profile.TimeZone

class Util {

    companion object {

        fun getPlaylistById(schedulePlaylists: ArrayList<SchedulePlaylist>, playlistId: Int): SchedulePlaylist {
            return schedulePlaylists.filter { it.id == playlistId }[0]
        }

        fun isInternetAvailable(): Boolean {
            return try {
                val p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com")
                return p1.waitFor() == 0
            } catch (e: Exception) {
                false
            }
        }

    }

}