package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.memeit.backend.models.HomeElement

abstract class MemeViewHolder(itemView: View, val memeAdapter: MemeAdapter) : RecyclerView.ViewHolder(itemView) {
    var itemPosition = 0
    var memeClickedListener: ((String) -> Unit)? = null
    abstract fun bind(homeElement: HomeElement)
}