package com.memeit.backend.models

import androidx.core.text.toSpannable
import com.memeit.backend.utils.generateFactory


sealed class Notification(val id: String,
                          val date: Long,
                          val seen: Boolean = false) {

    abstract val title: String
    open val message: String = ""

    companion object {
        const val GENERAL_TYPE = 0
        const val FOLLOWING_TYPE = 1
        const val REACTION_TYPE = 2
        const val COMMENT_TYPE = 3
        const val AWARd_TYPE = 4
        const val MEME_MENTION_TYPE = 5
        const val COMMENT_MENTION_TYPE = 6
        const val COMMENT_REPLY_TYPE = 7

        fun getRuntimeTypeAdapterFactory() =
                generateFactory(Notification::class.java,
                        listOf(
                                GeneralNotification::class.java,
                                FollowingNotification::class.java,
                                ReactionNotification::class.java,
                                CommentNotification::class.java,
                                AwardNotification::class.java,
                                MemeMentionNotification::class.java,
                                CommentMentionNotification::class.java,
                                CommentReplyNotification::class.java
                        ))
    }
}

class GeneralNotification(
        id: String,
        override val title: String,
        override val message: String,
        date: Long,
        seen: Boolean = false,
        val link: String? = null) : Notification(id, date, seen)

class FollowingNotification(
        id: String,
        date: Long,
        seen: Boolean = false,
        val followerUser: User) : Notification(id, date, seen) {

    override val title: String
        get() = "${followerUser.name} is now following you"
}

class ReactionNotification(
        id: String,
        val reactorUser: User,
        val reaction: Reaction,
        val meme: Meme,
        date: Long,
        seen: Boolean = false) : Notification(id, date, seen) {

    override val title: String
        get() = "${reactorUser.name} reacted to your meme"


}

class CommentNotification(
        id: String,
        val commenterUser: User,
        val comment: Comment,
        val meme: Meme,
        date: Long,
        seen: Boolean = false) : Notification(id, date, seen) {

    override val title: String = "${commenterUser.name} commented on your meme"
    override val message: String = comment.comment!!


}

class CommentReplyNotification(
        id: String,
        val replierUser: User,
        val reply: Reply,
        val comment: Comment,
        date: Long,
        seen: Boolean = false) : Notification(id, date, seen) {

    override val title: String
        get() = "${replierUser.name} replied on your comment"
    override val message: String
        get() = "\"${comment.comment}\"\n${reply.reply}"
}

class MemeMentionNotification(
        id: String,
        val mentionerUser: User,
        val meme: Meme,
        date: Long,
        seen: Boolean = false) : Notification(id, date, seen) {
    override val title: String
        get() = "${mentionerUser.name} mentioned you on a post"
}

class CommentMentionNotification(
        id: String,
        val mentionerUser: User,
        val comment: Comment,
        date: Long,
        seen: Boolean = false) : Notification(id, date, seen) {
    override val title: String = "${mentionerUser.name} mentioned you on a comment"
    override val message: String = comment.comment!!
}

class AwardNotification(
        id: String,
        badgeId: String,
        date: Long,
        seen: Boolean = false) : Notification(id, date, seen) {
    val badge = Badge.ofID(badgeId)
    override val title = "Congratulations!, You have achieved the ${badge.label} badge"
    override val message: String = badge.description
}

