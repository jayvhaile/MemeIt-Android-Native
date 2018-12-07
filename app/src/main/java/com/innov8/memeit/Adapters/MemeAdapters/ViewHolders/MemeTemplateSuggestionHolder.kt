package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Adapters.MemeAdapters.HomeMemeAdapter
import com.innov8.memeit.Adapters.TemplateSugAdapter
import com.innov8.memeit.R
import com.innov8.memeit.Utils.makeLinear
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.MemeTemplateSuggestion

class MemeTemplateSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)

    private val adapter: TemplateSugAdapter = TemplateSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Meme Templates to Edit"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.templatesPool)

    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as MemeTemplateSuggestion
        adapter.setAll(a.templates)
    }

}