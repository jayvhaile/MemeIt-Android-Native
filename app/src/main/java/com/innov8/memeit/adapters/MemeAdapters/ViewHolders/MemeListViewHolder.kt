package com.innov8.memeit.adapters.MemeAdapters.ViewHolders

import android.view.View
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.customViews.MemeView
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.Meme

class MemeListViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    init {
        itemView as MemeView
        itemView.memeClickedListener = {
            memeClickedListener?.invoke(it)
        }
        itemView.onRemoveMeme = {
            memeAdapter.remove(it)
        }
    }
    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        itemView as MemeView
        itemView.meme = meme
    }
}