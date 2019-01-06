package com.innov8.memeit.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.R
import com.innov8.memeit.commons.SimpleELEListAdapter
import com.innov8.memeit.commons.toast
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.utils.formatNumber
import com.innov8.memeit.utils.loadImage
import com.innov8.memeit.utils.prefix
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.OnCompleted
import com.memeit.backend.call
import com.memeit.backend.models.Meme
import com.memeit.backend.models.MyUser
import com.memeit.backend.models.User
import okhttp3.ResponseBody

class UserListAdapter(mContext: Context, override var emptyDescription: String) : SimpleELEListAdapter<User>(mContext, R.layout.list_item_follower) {
    override var emptyDrawableId: Int = R.drawable.empty_list
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var errorDescription: String = "Couldn't load Memes"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }
    private val myUser: MyUser? = MemeItClient.myUser

    init {
        if (onItemClicked == null)
            onItemClicked = {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", it)
                context.startActivity(i)
            }
    }

    override fun createViewHolder(view: View): MyViewHolder<User> {
        return UserListViewHolder(view)
    }

    internal fun isMe(id: String?): Boolean {
        return myUser!!.id == id
    }

    private fun getItemByID(id: String) = items.find { it.uid == id }

    inner class UserListViewHolder(itemView: View) : MyViewHolder<User>(itemView) {
        private val followerImage: ProfileDraweeView = itemView.findViewById(R.id.notif_icon)
        private val followerName: TextView = itemView.findViewById(R.id.follower_name)
        private val followerDetail: TextView = itemView.findViewById(R.id.follower_detail)
        private val followV: TextView = itemView.findViewById(R.id.follow_user)

        init {

            followV.visibility = View.VISIBLE
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

            itemView.setOnClickListener {
                onItemClicked?.invoke(items[item_position])
            }
        }

        override fun bind(t: User) {
            followerName.text = t.name
            followerDetail.text = t.postCount.formatNumber("post", "posts")
            followerImage.setText(t.name.prefix())
            followerImage.loadImage(t.imageUrl)
            if (isMe(t.uid!!)) {
                followV.visibility = View.GONE
            } else {
                followV.visibility = View.VISIBLE
                followV.text = if (t.isFollowedByMe) "Unfollow" else "Follow"
            }


        }
    }

}
