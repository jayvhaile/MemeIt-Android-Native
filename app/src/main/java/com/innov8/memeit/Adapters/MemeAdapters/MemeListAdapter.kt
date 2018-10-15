package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeListViewHolder
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.HomeElement

class MemeListAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != HomeElement.MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in MemeListAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme, parent, false)
        return MemeListViewHolder(view, this)
    }
}