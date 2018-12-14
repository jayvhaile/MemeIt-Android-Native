package com.innov8.memeit.Adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.Utils.formatNumber
import com.memeit.backend.models.Tag
import kotlin.Comparator

class TagSearchAdapter(context: Context) : ELEFilterableListAdapter<Tag, TagSearchAdapter.TagViewHolder>(context) {
    override val filterer: (Tag) -> Boolean = { it.tag.contains(filterWord) }
    override val sorter: Comparator<in Tag> = Comparator { tag1: Tag, tag2: Tag -> tag1.compareTo(tag2) }
    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "No tags Found"
    override var errorDescription: String = "Failed to load tags"
    override var errorActionText: String? = "Try Again"
    override var emptyActionText: String? = null
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(inflater.inflate(R.layout.list_item_tag_inline, parent, false))
    }

    override fun onBindHolder(holder: TagViewHolder, position: Int) {
        holder.bind(getItemAt(position))
    }

    override fun getItemType(position: Int): Int = 0
    var filterWord: String = ""
        set(value) {
            field = value
            filter()
            filterable = !field.isBlank()
        }

    fun updateFilter(value: String) {
        filterWord = value
        filter()
    }

    var onItemClicked: ((Tag) -> Unit)? = null

    inner class TagViewHolder(itemView: View) : MyViewHolder<Tag>(itemView) {
        private val tagV: TextView = itemView.findViewById(R.id.tag_view)
        private val tagCountV: TextView = itemView.findViewById(R.id.tag_count)

        init {
            itemView.setOnClickListener { onItemClicked?.invoke(getItemAt(item_position)) }
        }

        override fun bind(t: Tag) {
            tagV.text = t.tag.toLowerCase()
            tagCountV.text = t.count.formatNumber()
        }
    }

}