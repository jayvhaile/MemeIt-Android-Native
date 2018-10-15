package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Adapters.MemeAdapters.HomeMemeAdapter
import com.innov8.memeit.Adapters.UserSugAdapter
import com.innov8.memeit.R
import com.innov8.memeit.makeLinear
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.UserSuggestion

class UserSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: UserSugAdapter = UserSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "User Suggestions"
        memeAdapter as HomeMemeAdapter

        list.setRecycledViewPool(memeAdapter.usersPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as UserSuggestion
        adapter.setAll(a.users)
    }
}