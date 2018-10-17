package com.innov8.memeit.Loaders

import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.*

class MyNotificationLoader() : Loader<Notification> {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Notification>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getMyNotifications(skip, limit).call({
            onSuccess(it.map { n -> parseNotif(n) })
        }, onError)
    }

    private fun parseNotif(it: Map<String, Any>): Notification {
        val type: Int = (it["type"] as Double).toInt()
        return when (type) {
            Notification.FOLLOWING_TYPE -> parseFollowingNotif(it)
            Notification.REACTION_TYPE -> parseReactionNotif(it)
            Notification.COMMENT_TYPE -> parseCommentNotif(it)
            else -> parseGeneralNotif(it)
        }
    }

    private fun parseGeneralNotif(it: Map<String, Any>): Notification {
        return Notification(0,
                it["nid"] as String? ?: "",
                it["title"] as String? ?: "",
                it["message"] as String? ?: "",
                it["date"] as Long,
                it["seen"] as Boolean)
    }

    private fun parseCommentNotif(it: Map<String, Any>): CommentNotification {
        return CommentNotification(
                it["nid"] as String? ?: "",
                it["name"] as String? ?: "",
                it["pic"] as String? ?: "",
                it["uid"] as String,
                it["mid"] as String,
                it["img_url"] as String,
                Meme.MemeType.of((it["mtype"] as String?) ?: "image"),
                it["comment"] as String,
                (it["date"] as Double).toLong(),
                it["seen"] as Boolean
        )
    }

    private fun parseReactionNotif(it: Map<String, Any>): ReactionNotification {
        return ReactionNotification(
                it["nid"] as String? ?: "",
                it["name"] as String? ?: "",
                it["pic"] as String? ?: "",
                it["uid"] as String,
                it["mid"] as String,
                it["img_url"] as String,
                Meme.MemeType.of((it["mtype"] as String?) ?: "image"),
                (it["reaction"] as Double).toInt(),
                (it["date"] as Double).toLong(),
                it["seen"] as Boolean
        )
    }

    private fun parseFollowingNotif(it: Map<String, Any>): FollowingNotification {
        return FollowingNotification(
                it["nid"] as String? ?: "",
                it["name"] as String? ?: "",
                it["pic"] as String? ?: "",
                it["uid"] as String,
                (it["date"] as Double).toLong(),
                it["seen"] as Boolean)
    }


}