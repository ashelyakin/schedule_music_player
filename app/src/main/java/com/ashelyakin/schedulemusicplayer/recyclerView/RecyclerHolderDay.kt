package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.View
import com.ashelyakin.schedulemusicplayer.profile.Day
import kotlinx.android.synthetic.main.day.view.*

class RecyclerHolderDay(view: View): RecyclerHolder(view) {

    override fun bind(element: Any, position: Int) {
        val day = element as Day
        itemView.day_title.text = day.day
    }

}