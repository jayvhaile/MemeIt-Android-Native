package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.FrameLayout
import com.innov8.memeit.Adapters.MemeAdapters.GridMemeAdapter
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.CustomViews.MemeDraweeView
import com.innov8.memeit.R
import com.innov8.memeit.screenWidth
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme

class MemeGridViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val memeImageV: MemeDraweeView = itemView.findViewById(R.id.meme_image)
    val width = screenWidth / GridMemeAdapter.GRID_SPAN_COUNT

    init {
        val lp = FrameLayout.LayoutParams(width, width)
        memeImageV.layoutParams = lp
        memeImageV.onClick = { memeClickedListener?.invoke(getCurrentMeme().id!!) }
        memeImageV.autoPlayGif = false
    }

    override fun bind(homeElement: HomeElement) {
        val meme = homeElement as Meme
        memeImageV.loadMeme(meme, width)
    }

    private fun getCurrentMeme(): Meme {
        return memeAdapter.items[itemPosition] as Meme
    }
}