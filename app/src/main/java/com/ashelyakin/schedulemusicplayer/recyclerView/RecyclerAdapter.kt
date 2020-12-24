package com.ashelyakin.schedulemusicplayer.recyclerView

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.ashelyakin.schedulemusicplayer.MainActivity
import com.ashelyakin.schedulemusicplayer.R
import com.ashelyakin.schedulemusicplayer.util.TimezoneUtil
import com.ashelyakin.schedulemusicplayer.profile.*


class RecyclerAdapter(schedule: Schedule, private val timezonePlaylistsData: MutableLiveData<HashMap<Int, TimeZonePlaylist>>,
                      private val owner: LifecycleOwner): RecyclerView.Adapter<RecyclerHolder>(){

    //список элементов для recyclerview, состоящий из списоков дней, таймзон и плейлистов, преобразованных в одномерную форму
    private val items = ArrayList<Pair<Any, ItemType>>()

    //инициализация списка элементов для recyclerView
    init{
        for (day in schedule.days){
            items.add(Pair(day, ItemType.DAY))
            //сортировка списка timeZones
            val sortTimeZones = TimezoneUtil.sortTimeZones(day.timeZones as ArrayList<TimeZone>)

            for (timeZone in sortTimeZones){
                items.add(Pair(timeZone, ItemType.TIMEZONE))
                for (playlist in timeZone.playlists){
                    items.add(Pair(playlist, ItemType.PLAYLIST))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        var v = parent.rootView
        when (viewType) {
            ItemType.DAY.ordinal -> v = inflateItemByType(parent, R.layout.day)
            ItemType.TIMEZONE.ordinal -> v =inflateItemByType(parent, R.layout.timezone)
            ItemType.PLAYLIST.ordinal -> v = inflateItemByType(parent, R.layout.playlist)
        }
        return RecyclerHolder(v, timezonePlaylistsData, owner)
    }

    private fun inflateItemByType(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].second.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}