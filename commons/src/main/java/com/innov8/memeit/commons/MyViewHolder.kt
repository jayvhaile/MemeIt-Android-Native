package com.innov8.memeit.commons

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * Created by Jv on 7/21/2018.
 */

abstract class MyViewHolder<T>(itemView: View) : ViewHolder(itemView) {

    abstract fun bind(t: T)

}
