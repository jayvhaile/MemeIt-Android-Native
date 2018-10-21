package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.ybq.android.spinkit.style.CubeGrid

import com.innov8.memegenerator.Adapters.ListAdapter
import com.innov8.memegenerator.Adapters.MyViewHolder
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memegenerator.loading_button_lib.interfaces.OnAnimationEndListener
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.CustomClasses.FontTextView
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.innov8.memeit.*
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.dataclasses.MUser
import com.memeit.backend.dataclasses.User
import com.memeit.backend.MemeItUsers
import com.memeit.backend.OnCompleted
import okhttp3.ResponseBody

class UserListAdapter(mContext: Context,override var emptyDescription: String) : SimpleELEListAdapter<User>(mContext, R.layout.list_item_follower) {
    override var emptyDrawableId: Int = R.drawable.empty_list
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var errorDescription: String = "Couldn't load Memes"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }
    private val tf: Typeface = Typeface.createFromAsset(mContext.assets, FontTextView.asset)
    private val myUser: MUser? = MemeItClient.myUser
    internal var size: Float = mContext.resources.getDimension(R.dimen.profile_mini_size)


    override fun createViewHolder(view: View): MyViewHolder<User> {
        return UserListViewHolder(view)
    }

    private fun isMe(id: String?): Boolean {
        return myUser!!.id == id
    }

    inner class UserListViewHolder(itemView: View) : MyViewHolder<User>(itemView) {
        internal var followerImage: ProfileDraweeView
        internal var followerName: TextView
        internal var followerDetail: TextView
        internal var followButton: CircularProgressButton

        init {
            followerImage = itemView.findViewById(R.id.notif_icon)
            followerName = itemView.findViewById(R.id.follower_name)
            followButton = itemView.findViewById(R.id.follower_follow_btn)
            followerDetail = itemView.findViewById(R.id.follower_detail)
            followButton.setOnClickListener {
                val t = followButton.text.toString()
                followButton.startAnimation()
                val (uid) = getItemAt(item_position)
                if (t.equals("unfollow", ignoreCase = true)) {
                    MemeItUsers.unfollowUser(uid!!).enqueue(object : OnCompleted<ResponseBody>() {
                        override fun onSuccess(responseBody: ResponseBody) {
                            followButton.revertAnimation { followButton.text = "Follow" }
                        }

                        override fun onError(error: String) {
                            followButton.revertAnimation()
                            Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    MemeItUsers.followUser(uid!!).enqueue(object : OnCompleted<ResponseBody>() {
                        override fun onSuccess(responseBody: ResponseBody) {
                            followButton.revertAnimation { followButton.text = "Unfollow" }
                        }

                        override fun onError(error: String) {
                            followButton.revertAnimation()
                            Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            itemView.setOnClickListener {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", getItemAt(item_position))
                context.startActivity(i)
            }
            followButton.typeface = tf
        }

        override fun bind(user: User) {
            followerName.text = user.name
            followerDetail.text = user.postCount.toString() + " posts"
            followerImage.text = user.name.prefix()
            followerImage.loadImage(user.imageUrl, size, size)
            if (isMe(user.uid)) {
                followButton.visibility = View.GONE
            } else {
                followButton.visibility = View.VISIBLE
                if (user.isFollowedByMe) {
                    followButton.text = "Unfollow"
                } else {
                    followButton.text = "Follow"
                }
            }


        }
    }

}
