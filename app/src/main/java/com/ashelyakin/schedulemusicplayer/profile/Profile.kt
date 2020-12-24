package com.ashelyakin.schedulemusicplayer.profile

// To parse the JSON, install Klaxon and do:
//
//   val welcome6 = Welcome6.fromJson(jsonString)

import android.os.Parcelable
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

private val klaxon = Klaxon()

@Parcelize
data class Profile (
    val id: Int,
    val name: String,
    val schedule: Schedule
) : Parcelable {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<Profile>(json)
    }
}

@Parcelize
data class Schedule (
    val playlists: List<SchedulePlaylist>,
    val days: List<Day>
) : Parcelable

@Parcelize
data class Day (
    val day: String,
    val timeZones: List<TimeZone>
) : Parcelable

@Parcelize
data class TimeZone (
    val from: String,
    val to: String,
    val playlists: List<TimeZonePlaylist>
): Parcelable

@Parcelize
data class TimeZonePlaylist (
    @Json(name = "playlist_id")
    val playlistID: Int,

    var proportion: Int
): Parcelable

@Parcelize
data class SchedulePlaylist (
    val id: Int,
    val name: String,
    val files: List<File>,
    val duration: Int,
    val random: Boolean
): Parcelable

@Parcelize
data class File (
    val id: Int,

    @Json(name = "file_name")
    val fileName: String,

    val name: String,
    val size: Int,

    @Json(name = "md5_file")
    val md5File: String,

    val duration: Int,
    val order: Int
): Parcelable
