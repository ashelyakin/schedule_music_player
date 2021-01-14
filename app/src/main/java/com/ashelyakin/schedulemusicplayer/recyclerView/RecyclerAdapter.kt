package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashelyakin.schedulemusicplayer.R


class RecyclerAdapter(private val recyclerItems: ArrayList<Pair<Any, ItemType>>, private val changeProportionCallbacks: ChangeProportionCallbacks): RecyclerView.Adapter<RecyclerHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        var v = parent.rootView
        return when (viewType) {
            ItemType.DAY.ordinal -> {
                v = inflateItemByType(parent, R.layout.day)
                RecyclerHolderDay(v)
            }
            ItemType.TIMEZONE.ordinal -> {
                v = inflateItemByType(parent, R.layout.timezone)
                RecyclerHolderTimezone(v)
            }
            else-> {
                v = inflateItemByType(parent, R.layout.playlist)
                RecyclerHolderPlaylist(v, changeProportionCallbacks)
            }
        }
    }

    private fun inflateItemByType(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        return recyclerItems[position].second.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.bind(recyclerItems[position].first, position)
    }

    override fun getItemCount(): Int {
        return recyclerItems.size
    }
}