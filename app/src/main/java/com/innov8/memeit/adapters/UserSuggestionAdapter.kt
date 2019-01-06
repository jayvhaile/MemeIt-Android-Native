package com.innov8.memeit.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import com.innov8.memegenerator.adapters.ListAdapter
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.R
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.commons.toast
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.utils.loadImage
import com.innov8.memeit.utils.prefix
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.User

class UserSuggestionAdapter(context: Context) : ListAdapter<User>(context, R.layout.list_item_user_sug) {
    override fun createViewHolder(view: View): MyViewHolder<User> = UserSugViewHolder(view)

    private fun getItemByID(id: String) = items.find { it.uid == id }


    inner class UserSugViewHolder(itemView: View) : MyViewHolder<User>(itemView) {
        private val profileV: ProfileDraweeView = itemView.findViewById(R.id.user_sug_pp)
        private val nameV: TextView = itemView.findViewById(R.id.user_sug_name)
        private val followV: TextView = itemView.findViewById(R.id.follow_user)

        init {
            itemView.setOnClickListener {
                ProfileActivity.startWithUser(context, items[item_position])
            }
            followV.setOnClickListener { _ ->
                val uid = items[item_position].uid!!
                if (followV.text == "Unfollow") {
                    followV.text = "Unfollowing..."
                    MemeItUsers.unfollowUser(uid).call({
                        followV.text = "Follow"
                        getItemByID(uid)?.isFollowedByMe = false
                    }, {
                        context.toast("Failed to Unfollow")
                        followV.text = "Unfollow"
                    })
                } else {
                    followV.text = "Following..."
                    MemeItUsers.followUser(uid).call({
                        followV.text = "Unfollow"
                        getItemByID(uid)?.isFollowedByMe = true
                    }, {
                        context.toast("Failed to Follow")
                        followV.text = "Follow"
                    })
                }
            }
        }

        override fun bind(t: User) {
            profileV.setText(t.name.prefix())
            profileV.loadImage(t.imageUrl)
            nameV.text = t.name
            followV.text = if (t.isFollowedByMe) "Unfollow" else "Follow"
        }
    }
}