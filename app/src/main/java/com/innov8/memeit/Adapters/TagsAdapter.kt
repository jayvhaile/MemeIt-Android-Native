package com.innov8.memeit.Adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.innov8.memegenerator.adapters.ListAdapter
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.CustomClasses.CustomMethods
import com.innov8.memeit.R
import com.innov8.memeit.R.color.*
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.Tag
import com.memeit.backend.utilis.Listener
import okhttp3.ResponseBody

class TagsAdapter(context: Context) : ListAdapter<Tag>(context, R.layout.list_item_tags_new) {
    override fun createViewHolder(view: View): MyViewHolder<Tag> {
        return TagsViewHolder(view)
    }

    private val colors = listOf(blue, orange, greeny, purple)
            .map { context.resources.getColor(it) }

    private fun getColorAt(pos: Int): Int = colors[pos % colors.size]

    inner class TagsViewHolder(itemView: View) : MyViewHolder<Tag>(itemView) {
        private val overlay: View = itemView.findViewById(R.id.overlay)
        private val tagV: TextView = itemView.findViewById(R.id.tag)
        private val tagPostCountV: TextView = itemView.findViewById(R.id.tag_post_count)
        private val tagFollowV: TextView = itemView.findViewById(R.id.follow_tag)

        init {
            tagFollowV.setOnClickListener {
                val t = getItemAt(item_position)
                if (tagFollowV.text=="Unfollow")
                    MemeItUsers.getInstance().unFollowTags(t.tag, Listener<ResponseBody>(mContext,"unfollow fail"){
                        mContext.toast("unfollowed")
                        tagFollowV.text="Follow"
                    })
                else
                    MemeItUsers.getInstance().followTags(arrayOf(t.tag), Listener<ResponseBody>(mContext,"follow fail") {
                        mContext.toast("followed")
                        tagFollowV.text="Unfollow"
                    })
            }
        }

        override fun bind(t: Tag) {
            tagFollowV.text = if (t.followed) "Unfollow" else "Follow"
            overlay.setBackgroundColor(getColorAt(item_position))
            tagV.text = "#${t.tag}"
            tagPostCountV.text = CustomMethods.formatNumber(t.count, "post", "posts")
        }
    }
}