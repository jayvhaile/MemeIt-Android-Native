package com.innov8.memeit.Adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.innov8.memegenerator.adapters.ListAdapter
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.R
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.Tag
import com.memeit.backend.utilis.Listener
import okhttp3.ResponseBody

class TagsAdapter(context: Context) : ListAdapter<Tag>(context, R.layout.list_item_tags_new) {
    override fun createViewHolder(view: View): MyViewHolder<Tag> {
        return TagsViewHolder(view)
    }


    private val colors = context.resources.getStringArray(R.array.tag_colors)
            .map { Color.parseColor(it) }

    private fun getColor(tag: String): Int {
        val i = tag.toCharArray()
                .map { it.toInt() }
                .toIntArray()
                .sum()
        return colors[i % colors.size]
    }

    inner class TagsViewHolder(itemView: View) : MyViewHolder<Tag>(itemView) {
        private val overlay: View = itemView.findViewById(R.id.overlay)
        private val tagV: TextView = itemView.findViewById(R.id.tag)
        private val tagPostCountV: TextView = itemView.findViewById(R.id.tag_post_count)
        private val tagFollowV: TextView = itemView.findViewById(R.id.follow_tag)

        init {
            tagFollowV.setOnClickListener { _ ->
                val t = getItemAt(item_position)
                if (tagFollowV.text == "Unfollow")
                    MemeItUsers.getInstance().unFollowTags(t.tag, Listener<ResponseBody>(mContext, "unfollow fail") {
                        mContext.toast("unfollowed")
                                tagFollowV.text = "Follow"
                    })
                else
                    MemeItUsers.getInstance().followTags(arrayOf(t.tag), Listener<ResponseBody>(mContext, "follow fail") {
                        mContext.toast("followed")
                        tagFollowV.text = "Unfollow"
                    })
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