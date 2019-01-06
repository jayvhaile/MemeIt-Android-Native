package com.innov8.memeit.commons

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * Created by Jv on 7/21/2018.
 */

abstract class MyViewHolder<T>(itemView: View) : ViewHolder(itemView) {
    var item_position: Int = 0

    abstract fun bind(t: T)

    fun setPosition(position: Int) {
        this.item_position = position
    }
}
