package com.innov8.memeit.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.R
import com.innov8.memeit.Utils.*
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.memeit.backend.models.*

class NotificationAdapter(context: Context) : ELEListAdapter<Notification, NotificationViewHolder>(context) {
    override var emptyDrawableId: Int = R.drawable.ic_notifications_black_24dp
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "You have no notifications"
    override var errorDescription: String = "Couldn't load notifications"
    override var emptyActionText: String? = "Refresh"
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }

    val colors = listOf(R.color.blue, R.color.purple, R.color.orange, R.color.greeny, R.color.golden, R.color.dodger_blue)
            .map { it.color(context) }


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return when (viewType) {
            Notification.GENERAL_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                NotificationViewHolder(this, v)
            }

            Notification.FOLLOWING_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_follower, parent, false)
                FollowingNotifHolder(this, v)
            }
            Notification.MENTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_follower, parent, false)
                MentionNotifHolder(this, v)
            }

            Notification.REACTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_reaction, parent, false)
                ReactionNotifHolder(this, v)
            }

            Notification.COMMENT_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_comment, parent, false)
                CommentNotifHolder(this, v)
            }

            Notification.AWARd_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                AwardNotifHolder(this, v)
            }
            else -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                NotificationViewHolder(this, v)
            }
        }
    }

    override fun onBindHolder(holder: NotificationViewHolder, position: Int) {
        holder.itemPosition = position
        holder.bind(items[position])
    }


    override fun getCount(): Int = items.count()
    override fun getItemType(position: Int): Int = items[position].type


}


open class NotificationViewHolder(val notifAdapter: NotificationAdapter, itemView: View) : RecyclerView.ViewHolder(itemView) {
    val back: View = itemView.findViewById(R.id.notif_back)
    val dot: View = itemView.findViewById(R.id.notif_dot)
    val icon: SimpleDraweeView = itemView.findViewById(R.id.notif_icon)
    val date: TextView = itemView.findViewById(R.id.notif_date)
    val title: TextView = itemView.findViewById(R.id.notif_title)
    val message: TextView? = itemView.findViewById(R.id.notif_message)
    val context = notifAdapter.context
    val d: GradientDrawable = ResourcesCompat.getDrawable(context.resources, R.drawable.circle, null) as GradientDrawable

    var itemPosition: Int = 0
    open fun bind(notif: Notification) {
        d.setColor(notifAdapter.colors[notif.type])
        back.setBackgroundColor(if (notif.seen) Color.WHITE else Color.rgb(248, 248, 250))
        dot.background = d
        date.text = notif.date.formateAsDate()
        title.text = notif.title
        message?.text = notif.message
    }

}

class FollowingNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    init {
        itemView.setOnClickListener {
            val user = User(currentItem.followerId, currentItem.followerName, imageUrl = currentItem.followerPic)
            ProfileActivity.startWithUser(notifAdapter.context, user)
        }
    }

    private val currentItem: FollowingNotification
        get() = notifAdapter.items[itemPosition] as FollowingNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as FollowingNotification
        icon as ProfileDraweeView
        icon.setText(notif.title.prefix())
        icon.loadImage(notif.followerPic)
    }

}

class MentionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    init {
        itemView.setOnClickListener {
            CommentsActivity.startWithMemeId(notifAdapter.context, currentItem.memeId)
        }
        icon.setOnClickListener {
            val user = User(currentItem.mentionerId, currentItem.mentionerName, imageUrl = currentItem.mentionerPic)
            ProfileActivity.startWithUser(notifAdapter.context, user)
        }
    }

    private val currentItem: MentionNotification
        get() = notifAdapter.items[itemPosition] as MentionNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as MentionNotification
        icon as ProfileDraweeView
        icon.setText(notif.title.prefix())
        icon.loadImage(notif.mentionerPic)
    }

}

class ReactionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    private val reactionImage: ImageView = itemView.findViewById(R.id.notif_reaction_image)

    init {
        itemView.setOnClickListener { goToComment() }
        icon.setOnClickListener {
            val user = User(currentItem.reactorId, currentItem.reactorName, imageUrl = currentItem.reactorPic)
            ProfileActivity.startWithUser(notifAdapter.context, user)
        }
    }

    private fun goToComment() {
        CommentsActivity.startWithMemeId(notifAdapter.context, currentItem.memeId)
    }

    private val currentItem: ReactionNotification
        get() = notifAdapter.items[itemPosition] as ReactionNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as ReactionNotification
        icon as ProfileDraweeView
        icon.setText(notif.reactorName.prefix())
        icon.loadImage(notif.reactorPic)
        reactionImage.setImageDrawable(notif.getReaction().getDrawable())
    }

}

class CommentNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {

    init {
        itemView.setOnClickListener { goToComment() }
        icon.setOnClickListener {
            val user = User(currentItem.commentorId, currentItem.commenterName, imageUrl = currentItem.commenterPic)
            ProfileActivity.startWithUser(notifAdapter.context, user)
        }
    }

    private fun goToComment() {
        CommentsActivity.startWithMemeId(notifAdapter.context, currentItem.memeId)
    }


    private val currentItem: CommentNotification
        get() = notifAdapter.items[itemPosition] as CommentNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as CommentNotification
        icon as ProfileDraweeView
        icon.setText(notif.commenterName.prefix())
        icon.loadImage(notif.commenterPic)
    }

}

class AwardNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as AwardNotification
        icon.setImageResource(notif.badge.getDrawableId(notifAdapter.context))
    }
}
