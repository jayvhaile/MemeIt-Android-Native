package com.innov8.memeit.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.internal.ImageRequest
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.activities.CommentsActivity
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.R
import com.innov8.memeit.commons.TouchableSpan
import com.innov8.memeit.utils.*
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

    fun getDrawableIDForNotificationType(type: Int): Int {
        return when (type) {
            Notification.FOLLOWING_TYPE -> R.drawable.user_icon
            Notification.MEME_MENTION_TYPE -> R.drawable.ic_at
            Notification.COMMENT_MENTION_TYPE -> R.drawable.ic_at
            Notification.REACTION_TYPE -> R.drawable.laughing_inactive

            Notification.COMMENT_TYPE -> R.drawable.ic_comment

            Notification.AWARd_TYPE -> R.drawable.ic_badges
            else -> R.drawable.ic_notifications_black_24dp
        }
    }

    fun getColorForNotificationType(type: Int): Int {
        return when (type) {
            Notification.FOLLOWING_TYPE -> R.color.dodger_blue.color(context)
            Notification.MEME_MENTION_TYPE -> R.color.purple.color(context)
            Notification.COMMENT_MENTION_TYPE -> R.color.greeny.color(context)
            Notification.REACTION_TYPE -> R.color.blue.color(context)
            Notification.COMMENT_TYPE -> R.color.brown.color(context)
            Notification.AWARd_TYPE -> R.color.golden.color(context)
            else -> R.color.orange.color(context)
        }
    }


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
            Notification.MEME_MENTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_follower, parent, false)
                MemeMentionNotifHolder(this, v)
            }
            Notification.COMMENT_MENTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                CommentMentionNotifHolder(this, v)
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
    val dot: ImageView = itemView.findViewById(R.id.notif_dot)
    val icon: SimpleDraweeView = itemView.findViewById(R.id.notif_icon)
    val date: TextView = itemView.findViewById(R.id.notif_date)
    val title: TextView = itemView.findViewById(R.id.notif_title)
    val message: TextView? = itemView.findViewById(R.id.notif_message)
    val context = notifAdapter.context
    val d: GradientDrawable = ResourcesCompat.getDrawable(context.resources, R.drawable.circle, null) as GradientDrawable

    var itemPosition: Int = 0
    open fun bind(notif: Notification) {
        d.setColor(notifAdapter.getColorForNotificationType(notif.type))
        dot.background = d
        dot.setImageResource(notifAdapter.getDrawableIDForNotificationType(notif.type))
        date.text = notif.date.formateAsDate()
        title.text = notif.title
        message?.text = notif.message
    }

}

private val defColor = Color.parseColor("#ff5656")
private const val defSelectedColor = Color.LTGRAY

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
        title.text = applySpan(notif.title, notif.followerName)
        icon.setText(notif.title.prefix())
        icon.loadImage(notif.followerPic)
    }

}

class MemeMentionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    init {
        itemView.setOnClickListener {
            CommentsActivity.startWithMemeId(notifAdapter.context, currentItem.memeId)
        }
        icon.setOnClickListener {
            val user = User(currentItem.mentionerId, currentItem.mentionerName, imageUrl = currentItem.mentionerPic)
            ProfileActivity.startWithUser(notifAdapter.context, user)
        }
    }

    private val currentItem: MemeMentionNotification
        get() = notifAdapter.items[itemPosition] as MemeMentionNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as MemeMentionNotification
        title.text = applySpan(notif.title, notif.mentionerName, "post")
        icon as ProfileDraweeView
        icon.setText(notif.title.prefix())
        icon.loadImage(notif.mentionerPic)
    }

}

class CommentMentionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    init {
        itemView.setOnClickListener {
            CommentsActivity.startWithMemeId(notifAdapter.context, currentItem.memeId)
        }
        icon.setOnClickListener {
            val user = User(currentItem.mentionerId, currentItem.mentionerName, imageUrl = currentItem.mentionerPic)
            ProfileActivity.startWithUser(notifAdapter.context, user)
        }
    }

    private val currentItem: CommentMentionNotification
        get() = notifAdapter.items[itemPosition] as CommentMentionNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as CommentMentionNotification
        title.text = applySpan(notif.title, notif.mentionerName, "comment")

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


        title.text = applySpan(notif.title, notif.reactorName, "meme")
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

        title.text = applySpan(notif.title, notif.commenterName, "meme")

        icon as ProfileDraweeView
        icon.setText(notif.commenterName.prefix())
        icon.loadImage(notif.commenterPic)
    }

}

fun applySpan(text: String, vararg words: String): Spannable =
        text.toSpannable().apply {
            words.forEach {
                val i = text.indexOf(it)
                this[i..i + it.length] = ForegroundColorSpan(defColor)
                this[i..i + it.length] = StyleSpan(Typeface.BOLD)
                this[i..i + it.length] = RelativeSizeSpan(1.05f)
            }

        }


class AwardNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as AwardNotification
        icon.setImageResource(notif.badge.getDrawableId(notifAdapter.context))
    }
}
