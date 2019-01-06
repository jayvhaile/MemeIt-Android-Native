package com.innov8.memeit.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.ybq.android.spinkit.style.Wave
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.utils.CustomMethods
import com.innov8.memeit.R
import com.innov8.memeit.activities.TagMemesActivity
import com.innov8.memeit.commons.SimpleELEListAdapter
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag

class TagsAdapter(context: Context) : SimpleELEListAdapter<Tag>(context, R.layout.list_item_tags) {


    override var emptyDrawableId: Int = R.drawable.tag2
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "Tags Empty"
    override var errorDescription: String = "Failed to load tags"
    override var emptyActionText: String? = "Reload"
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = Wave().apply {
        color = Color.rgb(255, 100, 0)

    }

    var fillWidth = true


    override fun createViewHolder(view: View): MyViewHolder<Tag> {
        return TagsViewHolder(view)
    }


    private val colors = context.resources.getStringArray(R.array.tagColors)
            .map { Color.parseColor(it) }

    private fun getColor(tag: String): Int {
        val i = tag.toCharArray()
                .map { it.toInt() }
                .toIntArray()
                .sum()
        return colors[i % colors.size]
    }

    private fun getItemByTag(tag: String) = items.find { it.tag == tag }

    inner class TagsViewHolder(itemView: View) : MyViewHolder<Tag>(itemView) {
        private val overlay: View = itemView.findViewById(R.id.overlay)
        private val tagV: TextView = itemView.findViewById(R.id.tag)
        private val tagPostCountV: TextView = itemView.findViewById(R.id.tag_post_count)
        private val tagFollowV: TextView = itemView.findViewById(R.id.follow_tag)

        init {
            if (!fillWidth)
                itemView.layoutParams = itemView.layoutParams.apply {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            itemView.setOnClickListener {
                TagMemesActivity.startWithTag(context, getItemAt(item_position).tag)
            }
            tagFollowV.setOnClickListener { _ ->
                val t = getItemAt(item_position)
                if (tagFollowV.text == "Unfollow") {
                    tagFollowV.text = "Unfollowing..."
                    MemeItUsers.unfollowTag(t.tag).call({
                        tagFollowV.text = "Follow"
                        getItemByTag(t.tag)?.followed = false
                    }, {
                        context.toast("Failed to Unfollow")
                        tagFollowV.text = "Unfollow"

                    })
                } else {
                    tagFollowV.text = "Following..."
                    MemeItUsers.followTags(arrayOf(t.tag)).call({
                        tagFollowV.text = "Unfollow"
                        getItemByTag(t.tag)?.followed = true

                    }, {
                        context.toast("Failed to Follow")
                        tagFollowV.text = "Follow"

                    })
                }

            }
        }

        override fun bind(t: Tag) {
            tagFollowV.text = if (t.followed) "Unfollow" else "Follow"
            overlay.setBackgroundColor(getColor(t.tag))
            tagV.text = "#${t.tag}"
            tagPostCountV.text = CustomMethods.formatNumber(t.count, "post", "posts")
        }
    }
}