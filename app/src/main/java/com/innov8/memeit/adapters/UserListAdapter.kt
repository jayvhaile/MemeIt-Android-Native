package com.innov8.memeit.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memegenerator.adapters.MyViewHolder
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.R
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.utils.loadImage
import com.innov8.memeit.utils.prefix
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItUsers
import com.memeit.backend.OnCompleted
import com.memeit.backend.models.MyUser
import com.memeit.backend.models.User
import okhttp3.ResponseBody

class UserListAdapter(mContext: Context, override var emptyDescription: String, val showFollow: Boolean = true) : SimpleELEListAdapter<User>(mContext, R.layout.list_item_follower) {
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

    private fun isMe(id: String?): Boolean {
        return myUser!!.id == id
    }

    inner class UserListViewHolder(itemView: View) : MyViewHolder<User>(itemView) {
        private val followerImage: ProfileDraweeView = itemView.findViewById(R.id.notif_icon)
        private val followerName: TextView = itemView.findViewById(R.id.follower_name)
        private val followerDetail: TextView = itemView.findViewById(R.id.follower_detail)
        private val followButton: CircularProgressButton = itemView.findViewById(R.id.follower_follow_btn)

        init {
            if (showFollow) {
                followButton.visibility = View.VISIBLE
                followButton.setOnClickListener {
                    val t = followButton.text.toString()
                    followButton.startAnimation()
                    val (uid) = getItemAt(item_position)
                    if (t.equals("unfollow", ignoreCase = true)) {
                        MemeItUsers.unfollowUser(uid!!).enqueue(object : OnCompleted<ResponseBody>() {
                            override fun onSuccess(responseBody: ResponseBody) {
                                followButton.revertAnimation { followButton.text = "Follow" }
                            }

                            override fun onError(message: String) {
                                followButton.revertAnimation()
                                Toast.makeText(context, "Error $message", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        MemeItUsers.followUser(uid!!).enqueue(object : OnCompleted<ResponseBody>() {
                            override fun onSuccess(responseBody: ResponseBody) {
                                followButton.revertAnimation { followButton.text = "Unfollow" }
                            }

                            override fun onError(message: String) {
                                followButton.revertAnimation()
                                Toast.makeText(context, "Error $message", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
//                followButton.typeface = tf
            } else
                followButton.visibility = View.GONE

            itemView.setOnClickListener {
                onItemClicked?.invoke(getItemAt(item_position))
            }
        }

        override fun bind(t: User) {
            followerName.text = t.name
            followerDetail.text = "${t.postCount} posts"
            followerImage.setText(t.name.prefix())
            followerImage.loadImage(t.imageUrl)
            if (showFollow)
                if (isMe(t.uid)) {
                    followButton.visibility = View.GONE
                } else {
                    followButton.visibility = View.VISIBLE
                    if (t.isFollowedByMe) {
                        followButton.text = "Unfollow"
                    } else {
                        followButton.text = "Follow"
                    }
                }


        }
    }

}
