
package com.ashelyakin.schedulemusicplayer.util

import android.app.Activity
import android.net.Uri
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil.Companion.getCurrentTimezone
import com.ashelyakin.schedulemusicplayer.util.Util.Companion.getPlaylistById
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.File
import kotlin.collections.ArrayList

class MediaItemUtil {

    /*companion object {

        //добавляет трек в плеер, учитывая проприи плейлистов и перемешивание/сортировку
        fun addMediaItemToPlayer(activity: Activity, player: SimpleExoPlayer, schedule: Schedule, currentTrack: MediaItem?) {

            val currentTimezone = getCurrentTimezone(schedule) ?: return
            if (currentTimezone.playlists.isEmpty())
                return

            val playlistMap = getPlaylistMap(currentTimezone.playlists, schedule.playlists as ArrayList<SchedulePlaylist>)

            if (currentTrack == null) {
                addFirstTrack(activity, player, playlistMap, currentTimezone)
            } else {
                val playlists = currentTimezone.playlists
                for (i in playlists.indices) {
                    if (ifPlaylistContainsTrack(schedule.playlists, playlists[i], currentTrack)) {

                        var currentPlaylistAddedTracks =
                            PlaylistsPlayingPositions.getPlaylistPlayingIndex(playlists[i].playlistID).second
                        val playlistForAdding: TimeZonePlaylist

                        if (currentPlaylistAddedTracks < playlistMap[playlists[i].playlistID]!!.second) {
                            playlistForAdding = playlists[i]
                            ++currentPlaylistAddedTracks
                        } else {
                            playlistForAdding = if (i + 1 == playlists.size)
                                playlists[0]
                            else
                                playlists[i + 1]
                            currentPlaylistAddedTracks = 1

                            val currentPlaylistPosition = PlaylistsPlayingPositions.getPlaylistPlayingIndex(playlists[i].playlistID).first
                            if (playlistMap[playlists[i].playlistID]!!.first.size == currentPlaylistPosition + 1)
                                PlaylistsPlayingPositions.setPlaylistPlyingIndex(playlists[i].playlistID, currentPlaylistPosition + 1, 0)
                        }

                        val currentPlaylistPosition =
                            PlaylistsPlayingPositions.getPlaylistPlayingIndex(playlistForAdding.playlistID).first
                        val files = playlistMap[playlistForAdding.playlistID]!!.first
                        val trackForAdding = getTrackForAdding(files, currentPlaylistPosition)

                        val mediaItemToAdd = getMediaItem(activity, trackForAdding)
                        MediaItems.setMediaItemPlaylist(mediaItemToAdd.mediaId, playlistForAdding.playlistID)
                        PlaylistsPlayingPositions.setPlaylistPlyingIndex(playlistForAdding.playlistID, currentPlaylistPosition + 1, currentPlaylistAddedTracks)

                        activity.runOnUiThread {
                            player.addMediaItem(mediaItemToAdd)
                        }

                        break
                    }
                }

            }
        }

        private fun getMediaItem(activity: Activity, file: com.ashelyakin.schedulemusicplayer.profile.File): MediaItem {
            return MediaItem.fromUri(
                Uri.fromFile(
                    File(
                        activity.filesDir.absolutePath,
                        file.id.toString() + ".mp3"
                    )
                )
            )
        }

        fun getTrackIdFromMediaId(mediaId: String?): Int {
            return mediaId?.substringBeforeLast('.')?.substringAfterLast('/')?.toInt() ?: -1
        }

        private fun getTrackForAdding(
            files: List<com.ashelyakin.schedulemusicplayer.profile.File>,
            currentPlaylistPosition: Int
        ): com.ashelyakin.schedulemusicplayer.profile.File {
            return if (currentPlaylistPosition + 1 >= files.size)
                files[(currentPlaylistPosition + 1) % files.size]
            else
                files[currentPlaylistPosition + 1]
        }

        private fun addFirstTrack(activity: Activity, player: SimpleExoPlayer, playlistMap: HashMap<Int, Pair<List<com.ashelyakin.schedulemusicplayer.profile.File>, Int>>, currentTimezone: TimeZone) {

            val mediaItemToAdd = getMediaItem(activity, playlistMap[currentTimezone.playlists[0].playlistID]!!.first[0])
            MediaItems.setMediaItemPlaylist(mediaItemToAdd.mediaId, currentTimezone.playlists[0].playlistID)
            PlaylistsPlayingPositions.setPlaylistPlyingIndex(currentTimezone.playlists[0].playlistID, 0, 1)

            activity.runOnUiThread {
                player.addMediaItem(mediaItemToAdd)
            }
        }


        private fun ifPlaylistContainsTrack(
            schedulePlaylists: List<SchedulePlaylist>,
            timeZonePlaylist: TimeZonePlaylist,
            currentTrack: MediaItem
        ): Boolean {
            val trackId = getTrackIdFromMediaId(currentTrack.mediaId)
            val files = getPlaylistById(
                schedulePlaylists as ArrayList<SchedulePlaylist>,
                timeZonePlaylist.playlistID
            ).files
            for (i in files.indices) {
                if (trackId == files[i].id)
                    return true
            }
            return false
        }

        private var playlistMap: HashMap<Int, Pair<List<com.ashelyakin.schedulemusicplayer.profile.File>, Int>>? = null

        //возвращает HashMap, в котором key - id плейлиста, value - пара: список файлов в плейлисте, пропорция
        //если опция плейлиста random == true, то перемешивает список файлов, иначе сортирует по order
        private fun getPlaylistMap(timeZonePlaylists: List<TimeZonePlaylist>,
            schedulePlaylists: ArrayList<SchedulePlaylist>): HashMap<Int, Pair<List<com.ashelyakin.schedulemusicplayer.profile.File>, Int>> {
            var res = playlistMap
            if (playlistMap == null)
                res = HashMap()

            for (playlist in timeZonePlaylists) {
                if (res?.get(playlist.playlistID) == null) {
                    res = HashMap()
                    playlistMap = null
                    break
                }
            }

            for (playlist in timeZonePlaylists) {
                val schedulePlaylist = schedulePlaylists.find { it.id == playlist.playlistID }!!
                if (playlistMap == null) {
                    val files = if (schedulePlaylist.random) {
                        schedulePlaylist.files.shuffled()
                    } else
                        schedulePlaylist.files.sortedBy { it.order }
                    res!![playlist.playlistID] = Pair(files, Proportions.getProportion(playlist.hashCode()))
                } else
                    res!![playlist.playlistID] = Pair(res[playlist.playlistID]!!.first, Proportions.getProportion(playlist.hashCode()))
            }
            playlistMap = res
            return res!!
        }

    }*/
}
