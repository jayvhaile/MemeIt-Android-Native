package com.innov8.memeit.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.Tag
import java.util.*

class TagSuggestionAdapter(val context: Context) : RecyclerView.Adapter<TagSuggestionAdapter.TagViewHolder>() {


    private val items: MutableSet<Tag>
    private val filteredItems: MutableList<Tag>

    private var filter: String = ""
    fun updateFilter(value: String) {
        filter = value
        filter()
    }

    var OnItemClicked: ((Tag) -> Unit)? = null
    var OnDataChange: ((MutableList<Tag>) -> Unit)? = null

    init {
        items = hashSetOf()
        filteredItems = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_suggestion, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.item_position = position
        holder.bind(getItemAt(position))
    }


    override fun getItemCount(): Int {
        return filteredItems.size
    }

    fun filter() {
        filteredItems.clear()
        if (filter.isNotEmpty())
            items.filter {
                it.tag.toLowerCase().startsWith(filter.toLowerCase())
            }.sortedWith(Comparator<Tag> { o1, o2 -> o2.date.compareTo(o1.date) })
                    .toCollection(filteredItems)

        dataChanged()
    }

    fun addAll(items: List<Tag>) {
        if (items.size == 0) return
        val start = this.items.size
        this.items.addAll(items)
        filter()
    }

    fun clear() {
        items.clear()
        filteredItems.clear()
        filter = ""
        dataChanged()
    }

    fun setAll(items: List<Tag>) {
        this.items.clear()
        this.items.addAll(items)
        filter()
    }

    fun getItemAt(index: Int): Tag {
        return filteredItems[index]
    }

    fun dataChanged() {
        notifyDataSetChanged()
        OnDataChange?.invoke(filteredItems)
    }

    inner class TagViewHolder(itemView: View) : MyViewHolder<Tag>(itemView) {
        private val tagTextV: TextView = itemView.findViewById(R.id.list_hashtag)

        init {
            itemView.setOnClickListener { OnItemClicked?.invoke(getItemAt(item_position)) }
        }

        @SuppressLint("SetTextI18n")
        override fun bind(t: Tag) {
            tagTextV.text = "#${t.tag.toLowerCase()}"
        }
    }

}