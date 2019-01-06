package com.innov8.memeit.adapters.MemeAdapters

import android.content.Context
import android.view.ViewGroup
import com.innov8.memeit.R
import com.innov8.memeit.adapters.MemeAdapters.ViewHolders.AdHolder
import com.innov8.memeit.adapters.MemeAdapters.ViewHolders.MemeListViewHolder
import com.innov8.memeit.adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.customViews.MemeView
import com.memeit.backend.models.HomeElement

class TrendingMemeAdapter(context: Context) : MemeAdapter(context) {

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        return when (viewType) {
            HomeElement.MEME_TYPE -> {
                MemeListViewHolder(MemeView(context), this, true)
            }
            HomeElement.AD_TYPE -> {
                val v = inflater.inflate(R.layout.list_item_ad_holder, parent, false)
                AdHolder(v, this)
            }
            else -> {
                throw IllegalArgumentException("ViewType must be one of the two")
            }
        }
    }
}