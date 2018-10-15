package com.innov8.memeit.Adapters.MemeAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.MemeAdapters.ViewHolders.*
import com.innov8.memeit.R
import com.innov8.memeit.measure
import com.memeit.backend.dataclasses.HomeElement

class HomeMemeAdapter(context: Context) : MemeAdapter(context) {
    val usersPool = RecyclerView.RecycledViewPool()
    val tagsPool = RecyclerView.RecycledViewPool()
    val temlplatesPool = RecyclerView.RecycledViewPool()
    override fun createHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        when (viewType) {
            HomeElement.MEME_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val view = measure("inflate meme") { inflater.inflate(R.layout.list_item_meme, parent, false) }
                return MemeListViewHolder(view, this)
            }
            HomeElement.USER_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return UserSuggestionHolder(v, this)
            }
            HomeElement.TAG_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return TagSuggestionHolder(v, this)
            }
            HomeElement.MEME_TEMPLATE_SUGGESTION_TYPE -> {
                val inflater = LayoutInflater.from(context)
                val v = inflater.inflate(R.layout.list_item_list, parent, false)
                return MemeTemplateSuggestionHolder(v, this)
            }
            HomeElement.AD_TYPE -> {
                return measure("inflate") {
                    val inflater = measure("create flator") { LayoutInflater.from(context) }
                    val v = measure("flating") { inflater.inflate(R.layout.list_item_ad2, parent, false) }
                    measure("create") { AdHolder(v, this) }
                }
            }
            else -> {
                throw IllegalArgumentException("ViewType must be one of the four")
            }
        }
    }
}