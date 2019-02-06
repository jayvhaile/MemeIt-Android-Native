package com.innov8.memeit.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
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
import com.facebook.drawee.view.SimpleDraweeView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.R
import com.innov8.memeit.activities.CommentRepliesActivity
import com.innov8.memeit.activities.CommentsActivity
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.commons.ELEListAdapter
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.commons.prefix
import com.innov8.memeit.commons.views.MemeItTextView
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.utils.*
import com.memeit.backend.models.*
import com.memeit.backend.models.Notification.Companion.AWARd_TYPE
import com.memeit.backend.models.Notification.Companion.COMMENT_MENTION_TYPE
import com.memeit.backend.models.Notification.Companion.COMMENT_REPLY_TYPE
import com.memeit.backend.models.Notification.Companion.COMMENT_TYPE
import com.memeit.backend.models.Notification.Companion.FOLLOWING_TYPE
import com.memeit.backend.models.Notification.Companion.GENERAL_TYPE
import com.memeit.backend.models.Notification.Companion.MEME_MENTION_TYPE
import com.memeit.backend.models.Notification.Companion.REACTION_TYPE

class NotificationAdapter(context: Context) : ELEListAdapter<Notification, NotificationViewHolder<out Notification>>(context) {

    override var emptyDrawableId: Int = R.drawable.ic_notifications_black_24dp
    override var errorDrawableId: Int = R.drawable.ic_no_internet
    override var emptyDescription: String = "You have no notifications"
    override var errorDescription: String = "Couldn't load notifications"
    override var emptyActionText: String? = "Refresh"
    override var errorActionText: String? = "Try Again"
    override val loadingDrawable = CubeGrid().apply {
        color = Color.rgb(255, 100, 0)
    }


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder<out Notification> {
        return when (viewType) {
            GENERAL_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                GeneralNotifHolder(this, v)
            }

            FOLLOWING_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_follower, parent, false)
                FollowingNotifHolder(this, v)
            }
            MEME_MENTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_follower, parent, false)
                MemeMentionNotifHolder(this, v)
            }
            COMMENT_MENTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                CommentMentionNotifHolder(this, v)
            }

            REACTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_reaction, parent, false)
                ReactionNotifHolder(this, v)
            }

            COMMENT_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_comment, parent, false)
                CommentNotifHolder(this, v)
            }
            COMMENT_REPLY_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_comment, parent, false)
                CommentReplyNotifHolder(this, v)
            }
            AWARd_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif, parent, false)
                AwardNotifHolder(this, v)
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun onBindHolder(holder: NotificationViewHolder<out Notification>, position: Int) {
        holder.bind(items[position])
    }


    override fun getCount(): Int = items.count()
    override fun getItemType(position: Int): Int = items[position].getViewType()


}

private val defColor = Color.parseColor("#ff5656")
private const val defSelectedColor = Color.LTGRAY


sealed class NotificationViewHolder<T : Notification>(val notifAdapter: NotificationAdapter, itemView: View) : MyViewHolder<Notification>(itemView) {
    val dot: ImageView = itemView.findViewById(R.id.notif_dot)
    val icon: SimpleDraweeView = itemView.findViewById(R.id.notif_icon)
    val date: TextView = itemView.findViewById(R.id.notif_date)
    val title: TextView = itemView.findViewById(R.id.notif_title)
    val message: MemeItTextView? = itemView.findViewById(R.id.notif_message)
    val context = notifAdapter.context
    val d: GradientDrawable = ResourcesCompat.getDrawable(context.resources, R.drawable.circle, null) as GradientDrawable

    init {
        itemView.setOnClickListener {
            onItemClicked()
        }
        message?.onLinkClicked = generateTextLinkActions(notifAdapter.context)
    }

    protected val currentItem: T
        @Suppress("UNCHECKED_CAST")
        get() = notifAdapter.items[adapterPosition] as T


    override fun bind(t: Notification) {
        d.setColor(t.getColorForNotificationType(notifAdapter.context))
        dot.background = d
        dot.setImageResource(t.getDrawableIDForNotificationType())
        date.text = t.date.formateAsDate()
        title.text = t.title
        message?.text = t.message
        bindItem(t as T)
    }

    abstract fun bindItem(t: T)

    abstract fun onItemClicked()
}

class GeneralNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<GeneralNotification>(notifAdapter, itemView) {

    override fun bindItem(t: GeneralNotification) {
        super.bind(t)
        icon.visibleBy(false)
    }

    override fun onItemClicked() {
        currentItem.link?.let {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(it.trim())
            })
        }
    }

}

class FollowingNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<FollowingNotification>(notifAdapter, itemView) {

    override fun bindItem(t: FollowingNotification) {
        icon as ProfileDraweeView
        title.text = applySpan(t.title, t.followerUser.name!!)
        icon.setText(t.title.prefix())
        icon.loadImage(t.followerUser.imageUrl)
    }

    override fun onItemClicked() {
        ProfileActivity.startWithUser(notifAdapter.context, currentItem.followerUser)
    }

}

class MemeMentionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<MemeMentionNotification>(notifAdapter, itemView) {
    init {
        icon.setOnClickListener {
            ProfileActivity.startWithUser(notifAdapter.context, currentItem.mentionerUser)
        }
    }

    override fun bindItem(t: MemeMentionNotification) {
        title.text = applySpan(t.title, t.mentionerUser.name!!, "post")
        icon as ProfileDraweeView
        icon.setText(t.title.prefix())
        icon.loadImage(t.mentionerUser.imageUrl)
    }

    override fun onItemClicked() {
        CommentsActivity.startWithMeme(notifAdapter.context, currentItem.meme)
    }
}

class CommentMentionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<CommentMentionNotification>(notifAdapter, itemView) {
    init {
        icon.setOnClickListener {
            ProfileActivity.startWithUser(notifAdapter.context, currentItem.mentionerUser)
        }
    }

    override fun bindItem(t: CommentMentionNotification) {
        title.text = applySpan(t.title, t.mentionerUser.name!!, "comment")

        icon as ProfileDraweeView
        icon.setText(t.title.prefix())
        icon.loadImage(t.mentionerUser.imageUrl)
    }

    override fun onItemClicked() {
        CommentRepliesActivity.start(notifAdapter.context, currentItem.comment)
    }

}

class ReactionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<ReactionNotification>(notifAdapter, itemView) {
    private val reactionImage: ImageView = itemView.findViewById(R.id.notif_reaction_image)

    init {
        icon.setOnClickListener {
            ProfileActivity.startWithUser(notifAdapter.context, currentItem.reactorUser)
        }
    }

    override fun onItemClicked() {
        CommentsActivity.startWithMeme(notifAdapter.context, currentItem.meme)
    }

    override fun bindItem(t: ReactionNotification) {
        title.text = applySpan(t.title, t.reactorUser.name!!, "meme")
        icon as ProfileDraweeView
        icon.setText(t.reactorUser.name.prefix())
        icon.loadImage(t.reactorUser.imageUrl)
        reactionImage.setImageDrawable(t.reaction.getDrawable())
    }

}

class CommentNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<CommentNotification>(notifAdapter, itemView) {

    init {
        icon.setOnClickListener {
            ProfileActivity.startWithUser(notifAdapter.context, currentItem.commenterUser)
        }
    }

    override fun onItemClicked() {
        CommentsActivity.startWithMeme(notifAdapter.context, currentItem.meme)
    }

    override fun bindItem(t: CommentNotification) {
        title.text = applySpan(t.title, t.commenterUser.name!!, "meme")
        icon as ProfileDraweeView
        icon.setText(t.commenterUser.name.prefix())
        icon.loadImage(t.commenterUser.imageUrl)
    }
}

class CommentReplyNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<CommentReplyNotification>(notifAdapter, itemView) {

    init {
        icon.setOnClickListener {
            ProfileActivity.startWithUser(notifAdapter.context, currentItem.replierUser)
        }
    }

    override fun onItemClicked() {
        CommentRepliesActivity.start(notifAdapter.context, currentItem.comment)
    }

    override fun bindItem(t: CommentReplyNotification) {
        title.text = applySpan(t.title, t.replierUser.name!!, "comment")
        icon as ProfileDraweeView
        icon.setText(t.replierUser.name.prefix())
        icon.loadImage(t.replierUser.imageUrl)
    }
}

class AwardNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder<AwardNotification>(notifAdapter, itemView) {
    override fun onItemClicked() {

    }

    override fun bindItem(t: AwardNotification) {
        icon.setImageResource(t.badge.getDrawableId(notifAdapter.context))
    }
}


private fun applySpan(text: String, vararg words: String): Spannable =
        text.toSpannable().apply {
            words.forEach {
                val i = text.indexOf(it)
                this[i..i + it.length] = ForegroundColorSpan(defColor)
                this[i..i + it.length] = StyleSpan(Typeface.BOLD)
                this[i..i + it.length] = RelativeSizeSpan(1.05f)
            }
        }

private fun Notification.getDrawableIDForNotificationType(): Int {
    return when (this) {
        is FollowingNotification -> R.drawable.user_icon
        is MemeMentionNotification, is CommentMentionNotification -> R.drawable.ic_at
        is ReactionNotification -> R.drawable.laughing_inactive
        is CommentNotification -> R.drawable.ic_comment
        is CommentReplyNotification -> R.drawable.ic_reply_black_24dp
        is AwardNotification -> R.drawable.ic_badges
        is GeneralNotification -> R.drawable.ic_notifications_black_24dp
    }
}

private fun Notification.getColorForNotificationType(context: Context): Int {
    return when (this) {
        is FollowingNotification -> R.color.dodger_blue.color(context)
        is MemeMentionNotification -> R.color.purple.color(context)
        is CommentMentionNotification -> R.color.purple.color(context)
        is ReactionNotification -> R.color.greeny.color(context)
        is CommentNotification -> R.color.blue.color(context)
        is CommentReplyNotification -> R.color.brown.color(context)
        is AwardNotification -> R.color.golden.color(context)
        is GeneralNotification -> R.color.orange.color(context)
    }
}

private fun Notification.getViewType(): Int {
    return when (this) {
        is FollowingNotification -> FOLLOWING_TYPE
        is MemeMentionNotification -> MEME_MENTION_TYPE
        is CommentMentionNotification -> COMMENT_MENTION_TYPE
        is ReactionNotification -> REACTION_TYPE
        is CommentNotification -> COMMENT_TYPE
        is CommentReplyNotification -> COMMENT_REPLY_TYPE
        is AwardNotification -> AWARd_TYPE
        is GeneralNotification -> GENERAL_TYPE
    }
}
