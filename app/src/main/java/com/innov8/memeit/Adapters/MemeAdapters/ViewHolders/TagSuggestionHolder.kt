package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Adapters.MemeAdapters.HomeMemeAdapter
import com.innov8.memeit.Adapters.TagsAdapter
import com.innov8.memeit.R
import com.innov8.memeit.makeLinear
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.TagSuggestion

class TagSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: TagsAdapter = TagsAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Recommended Tags"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.tagsPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as TagSuggestion
        adapter.setAll(a.tags)
    }
}