package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomViews.MemeView
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme

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