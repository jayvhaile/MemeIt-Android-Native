package com.innov8.memeit.adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.adapters.MemeAdapters.HomeMemeAdapter
import com.innov8.memeit.adapters.TemplateAdapter
import com.innov8.memeit.R
import com.innov8.memeit.adapters.TemplateSuggestionAdapter
import com.innov8.memeit.utils.makeLinear
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.MemeTemplateSuggestion

class MemeTemplateSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)

    private val adapter by lazy {
        TemplateSuggestionAdapter(memeAdapter.context)
    }

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Meme templates to try out"
        memeAdapter as HomeMemeAdapter
        list.setRecycledViewPool(memeAdapter.templatesPool)

    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as MemeTemplateSuggestion
        adapter.setAll(a.templates)
    }

}