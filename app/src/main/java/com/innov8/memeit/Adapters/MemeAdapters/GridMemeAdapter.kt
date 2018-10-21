package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeGridViewHolder
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.MemeViewHolder
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.HomeElement

class GridMemeAdapter(context: Context) : MemeAdapter(context) {
    companion object {
        const val GRID_SPAN_COUNT = 3
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        if (viewType != HomeElement.MEME_TYPE)
            throw IllegalStateException("View Type must only be MEME_TYPE in GridMemeAdapter")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_meme_grid, parent, false)
        return MemeGridViewHolder(view, this)
    }

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        val lm = GridLayoutManager(context, GRID_SPAN_COUNT, RecyclerView.VERTICAL, false)

        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (getItemViewType(position)) {
                    TYPE_EMPTY, TYPE_ERROR, TYPE_LOADING, TYPE_LOAD_MORE -> GRID_SPAN_COUNT
                    else -> 1
                }
            }
        }
        return lm
    }


}