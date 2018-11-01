package com.innov8.memeit.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.github.ybq.android.spinkit.style.CubeGrid
import com.innov8.memeit.*
import com.innov8.memeit.Activities.CommentsActivity
import com.innov8.memeit.Activities.ProfileActivity
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.memeit.backend.dataclasses.*

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

    val colors = listOf(R.color.blue, R.color.purple, R.color.orange, R.color.greeny)
            .map { idToColor(it) }

    fun idToColor(id: Int): Int =
            context.resources.getColor(id)


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

            Notification.REACTION_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_meme, parent, false)
                ReactionNotifHolder(this, v)
            }

            Notification.COMMENT_TYPE -> {
                val v = LayoutInflater.from(context).inflate(R.layout.list_item_notif_meme, parent, false)
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
    val d: Drawable = VectorDrawableCompat.create(context.resources, R.drawable.circle, null)!!
    var itemPosition: Int = 0
    open fun bind(notif: Notification) {
        back.setBackgroundColor(if (notif.seen) Color.WHITE else Color.rgb(248, 248, 250))
        d.setColorFilter(notifAdapter.colors[notif.type], PorterDuff.Mode.SRC)
        dot.background = d
        date.text = notif.date.formateAsDate()
        title.text = notif.title
        message?.text = notif.message
    }

}

class FollowingNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    init {
        itemView.setOnClickListener {
            val i = Intent(notifAdapter.context, ProfileActivity::class.java)
            val n = getCurrentItem()
            i.putExtra("user", User(n.followerId, n.followerName, n.followerPic))
            notifAdapter.context.startActivity(i)
        }
    }

    private fun getCurrentItem(): FollowingNotification = notifAdapter.items[itemPosition] as FollowingNotification

    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as FollowingNotification
        icon as ProfileDraweeView
        icon.text = notif.title.prefix()
        icon.loadImage(notif.followerPic)
    }

}

class ReactionNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    val memeImage: SimpleDraweeView = itemView.findViewById(R.id.meme_image)

    init {
        itemView.setOnClickListener {
            val n = getCurrentItem()
            val meme = Meme(n.memeId, imageId = n.memePic)
            val intent = Intent(notifAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            notifAdapter.context.startActivity(intent)
        }
        icon.setOnClickListener {
            val i = Intent(notifAdapter.context, ProfileActivity::class.java)
            val n = getCurrentItem()
            i.putExtra("user", User(n.reactorId, n.reactorName, n.reactorPic))
            notifAdapter.context.startActivity(i)
        }
    }

    private fun getCurrentItem(): ReactionNotification = notifAdapter.items[itemPosition] as ReactionNotification
    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as ReactionNotification
        icon as ProfileDraweeView
        icon.text = notif.reactorName.prefix()
        icon.loadImage(notif.reactorPic)
        memeImage.loadMeme(notif.getMeme())
        val span = ImageSpan(notif.getReaction().getDrawable())
        val s = SpannableString(" ")
        s.setSpan(span, 0, 1, 0)
        message?.text = s

    }

}

class CommentNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    val memeImage: SimpleDraweeView = itemView.findViewById(R.id.meme_image)

    init {
        itemView.setOnClickListener {
            val n = getCurrentItem()
            val meme = Meme(n.memeId, imageId = n.memePic)
            val intent = Intent(notifAdapter.context, CommentsActivity::class.java)
            intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme)
            notifAdapter.context.startActivity(intent)
        }
        icon.setOnClickListener {
            val i = Intent(notifAdapter.context, ProfileActivity::class.java)
            val n = getCurrentItem()
            i.putExtra("user", User(n.commentorId, n.commenterName, n.commenterPic))
            notifAdapter.context.startActivity(i)
        }
    }

    private fun getCurrentItem(): CommentNotification = notifAdapter.items[itemPosition] as CommentNotification
    override fun bind(notif: Notification) {
        super.bind(notif)
        notif as CommentNotification
        icon as ProfileDraweeView
        icon.text = notif.commenterName.prefix()
        icon.loadImage(notif.commenterPic)
        memeImage.loadMeme(notif.getMeme())
    }

}

class AwardNotifHolder(notifAdapter: NotificationAdapter, itemView: View) : NotificationViewHolder(notifAdapter, itemView) {
    override fun bind(notif: Notification) {
        super.bind(notif)
    }
}
