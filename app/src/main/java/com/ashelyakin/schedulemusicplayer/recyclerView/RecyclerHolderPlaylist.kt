package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.View
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import com.ashelyakin.schedulemusicplayer.profile.TimeZonePlaylist
import com.ashelyakin.schedulemusicplayer.util.Util
import kotlinx.android.synthetic.main.playlist.view.*

class RecyclerHolderPlaylist(view: View, private val changeProportionCallbacks: ChangeProportionCallbacks): RecyclerHolder(view)  {

    private var playlistPosition = 0

    override fun bind(element: Any, position: Int) {
        val timeZonePlaylist = element as TimeZonePlaylist

        val schedulePlaylist = SchedulePlaylistsData.getPlaylistsData(timeZonePlaylist.playlistID)
        itemView.playlist_name.text = schedulePlaylist?.name
        itemView.proportion.text = timeZonePlaylist.proportion.toString()
        playlistPosition = position

        //отображаем предупреждение о нецелостности плейлиста
        if (!Util.isPlaylistIntegrity(schedulePlaylist, itemView.context.filesDir.absolutePath))
            itemView.alert.visibility = View.VISIBLE

        itemView.proportion_minus.setOnClickListener{
            changeProportionCallbacks.minusProportion(playlistPosition)
        }
        itemView.proportion_plus.setOnClickListener{changeProportionCallbacks.plusProportion(playlistPosition)}
    }

}