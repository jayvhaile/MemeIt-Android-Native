package com.innov8.memegenerator.adapters

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Jv on 7/21/2018.
 */

abstract class MyViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected var item_position: Int = 0

    abstract fun bind(t: T, position: Int)

    fun setPosition(position: Int) {
        this.item_position = position
    }
}
