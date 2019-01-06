package com.innov8.memeit.adapters.MemeAdapters

import android.content.Context
import android.view.ViewGroup
import com.innov8.memeit.adapters.MemeAdapters.ViewHolders.MemeListViewHolder
import com.innov8.memeit.adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.customViews.MemeView
import com.innov8.memeit.R
import com.memeit.backend.models.HomeElement

class MemeListAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    }
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != HomeElement.MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in MemeListAdapter")
        return MemeListViewHolder(MemeView(context), this)
    }
}
