package com.ashelyakin.schedulemusicplayer.profile

import android.content.Context
import android.util.Log
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

class ProfileLoader {

    companion object {

        fun getProfileFromJson(context: Context, jsonFileName: String): Profile {
            val json = getJsonDataFromAsset(context, jsonFileName)
            if (json == null) {
                Log.e("ProfileLoader", "Error loading json")
                exitProcess(0)
            }
            val profile = Profile.fromJson(json)
            if (profile == null) {
                Log.e("ProfileLoader", "Error loading profile")
                exitProcess(0)
            }
            initPlaylistDataAndSetId(profile.schedule)
            return profile
        }

        private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        private var uuid = AtomicInteger(0)
        private fun initPlaylistDataAndSetId(schedule: Schedule) {
            for (day in schedule.days)
                for (timezone in day.timeZones)
                    for (playlist in timezone.playlists){
                        val newID = uuid.getAndIncrement()
                        val schedulePlaylist = schedule.playlists.filter { it.id == playlist.playlistID }[0]
                        shuffleOrSortFilesInPlaylist(schedulePlaylist)
                        SchedulePlaylistsData.setPlaylistsData(newID, schedulePlaylist)
                        playlist.playlistID = newID
                    }
        }

        private fun shuffleOrSortFilesInPlaylist(schedulePlaylist: SchedulePlaylist) {
            if (schedulePlaylist.random) {
                schedulePlaylist.files = schedulePlaylist.files.shuffled()
            } else
                schedulePlaylist.files = schedulePlaylist.files.sortedBy { it.order }
        }
    }
}