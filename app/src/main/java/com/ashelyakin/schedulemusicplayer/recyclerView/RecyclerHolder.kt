package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ashelyakin.schedulemusicplayer.R
import com.ashelyakin.schedulemusicplayer.profile.Day
import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.MD5
import com.ashelyakin.schedulemusicplayer.util.Util
import java.io.File


class RecyclerHolder(view: View, private val schedulePlaylists: ArrayList<SchedulePlaylist>): RecyclerView.ViewHolder(view) {

    fun bind(item: Pair<Any, ItemType>) {
        when (item.second.ordinal){
            ItemType.DAY.ordinal -> bindDay(item.first as Day)
            ItemType.TIMEZONE.ordinal -> bindTimezone(item.first as TimeZone)
            ItemType.PLAYLIST.ordinal -> bindPlaylist(item.first as TimeZonePlaylist)
        }
    }

    private fun bindDay(day: Day){
        val dayTitle = itemView.findViewById<TextView>(R.id.day_title)
        dayTitle.text = day.day
    }

    private fun bindTimezone(timeZone: TimeZone){
        val time = itemView.findViewById<TextView>(R.id.time)
        time.text = timeZone.from + "-" + timeZone.to
    }

    private fun bindPlaylist(timeZonePlaylist: TimeZonePlaylist){
        val schedulePlaylist = Util.getPlaylistById(schedulePlaylists, timeZonePlaylist.playlistID)
        itemView.findViewById<TextView>(R.id.playlist_name).text = schedulePlaylist.name
        itemView.findViewById<TextView>(R.id.proportion).text = Proportions.getProportion(timeZonePlaylist.hashCode()).toString()

        //отображаем предупреждение о нецелостности плейлиста
        if (!isPlaylistIntegrity(schedulePlaylist))
            itemView.findViewById<ImageView>(R.id.alert).isVisible = true
    }

    private fun isPlaylistIntegrity(schedulePlaylist: SchedulePlaylist): Boolean{
        var res = true
        val fileDirPath = itemView.context.filesDir.absolutePath
        schedulePlaylist.files.map {
            val musicFile = File(fileDirPath, it.id.toString() + ".mp3")
            if (!MD5.checkMD5(it.md5File, musicFile)){
                res = false
                musicFile.delete()
            }
        }
        return res
    }
}
