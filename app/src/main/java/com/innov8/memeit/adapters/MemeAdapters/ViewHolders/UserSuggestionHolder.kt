package com.innov8.memeit.adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.adapters.MemeAdapters.HomeMemeAdapter
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.adapters.UserSugAdapter
import com.innov8.memeit.R
import com.innov8.memeit.utils.makeLinear
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.UserSuggestion

class UserSuggestionHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    val list: RecyclerView = itemView.findViewById(R.id.list_recyc)
    val title: TextView = itemView.findViewById(R.id.list_title)
    private val adapter: UserSugAdapter = UserSugAdapter(memeAdapter.context)

    init {
        list.makeLinear(RecyclerView.HORIZONTAL)
        list.adapter = adapter
        title.text = "Suggestions for You"
        memeAdapter as HomeMemeAdapter

        list.setRecycledViewPool(memeAdapter.usersPool)
    }

    override fun bind(homeElement: HomeElement) {
        val a = homeElement as UserSuggestion
        adapter.setAll(a.users)
    }
}