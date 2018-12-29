package com.innov8.memeit.adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.FrameLayout
import com.innov8.memeit.adapters.MemeAdapters.GridMemeAdapter
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.customViews.MemeDraweeView
import com.innov8.memeit.R
import com.innov8.memeit.utils.screenWidth
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.Meme

class MemeGridViewHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val memeImageV: MemeDraweeView = itemView.findViewById(R.id.meme_image)
    val width get() = screenWidth / GridMemeAdapter.GRID_SPAN_COUNT

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