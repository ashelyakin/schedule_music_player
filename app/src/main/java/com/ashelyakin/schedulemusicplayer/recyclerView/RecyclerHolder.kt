package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import com.ashelyakin.schedulemusicplayer.profile.Day
import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.MD5
import kotlinx.android.synthetic.main.day.view.*
import kotlinx.android.synthetic.main.playlist.view.*
import kotlinx.android.synthetic.main.timezone.view.*
import java.io.File


class RecyclerHolder(view: View, private val timezonePlaylistsData: MutableLiveData<HashMap<Int, TimeZonePlaylist>>,
                     private val owner: LifecycleOwner): RecyclerView.ViewHolder(view) {

    fun bind(item: Pair<Any, ItemType>) {
        when (item.second.ordinal){
            ItemType.DAY.ordinal -> bindDay(item.first as Day)
            ItemType.TIMEZONE.ordinal -> bindTimezone(item.first as TimeZone)
            ItemType.PLAYLIST.ordinal -> bindPlaylist(item.first as TimeZonePlaylist)
        }
    }

    private fun bindDay(day: Day){
        itemView.day_title.text = day.day
    }

    private fun bindTimezone(timeZone: TimeZone){
        itemView.time.text = timeZone.from + "-" + timeZone.to
    }

    private fun bindPlaylist(timeZonePlaylist: TimeZonePlaylist){
        val schedulePlaylist = SchedulePlaylistsData.getPlaylistsData(timeZonePlaylist.playlistID)
        itemView.playlist_name.text = schedulePlaylist?.name
        itemView.proportion.tag = timeZonePlaylist.hashCode()
        timezonePlaylistsData.observe(owner, Observer {
            itemView.proportion.text = it[timeZonePlaylist.hashCode()]?.proportion.toString()
        })

        //отображаем предупреждение о нецелостности плейлиста
        if (!isPlaylistIntegrity(schedulePlaylist))
            itemView.alert.visibility = View.VISIBLE
    }

    private fun isPlaylistIntegrity(schedulePlaylist: SchedulePlaylist?): Boolean{
        if (schedulePlaylist == null)
            return true
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
