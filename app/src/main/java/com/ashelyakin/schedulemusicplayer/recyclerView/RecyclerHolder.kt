package com.ashelyakin.schedulemusicplayer.recyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class RecyclerHolder(view: View): RecyclerView.ViewHolder(view){

    abstract fun bind(element: Any, position: Int)

}