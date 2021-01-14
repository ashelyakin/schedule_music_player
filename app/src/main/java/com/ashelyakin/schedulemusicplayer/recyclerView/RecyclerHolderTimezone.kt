package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.View
import com.ashelyakin.schedulemusicplayer.profile.TimeZone
import kotlinx.android.synthetic.main.timezone.view.*

class RecyclerHolderTimezone(view: View): RecyclerHolder(view)  {

    override fun bind(element: Any, position: Int) {
        val timeZone = element as TimeZone
        itemView.time.text = timeZone.from + "-" + timeZone.to
    }

}