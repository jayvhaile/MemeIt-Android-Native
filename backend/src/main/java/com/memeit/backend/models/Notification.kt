package com.memeit.backend.models


open class Notification(val type: Int = 0,
                        val id: String,
                        val title: String,
                        val message: String,
                        val date: Long,
                        val seen: Boolean = false) {

    companion object {
        const val GENERAL_TYPE = 0
        const val FOLLOWING_TYPE = 1
        const val REACTION_TYPE = 2
        const val COMMENT_TYPE = 3
        const val AWARd_TYPE = 4
        const val MENTION_TYPE = 5

        fun parseNotif(it: Map<String, Any>): Notification {
            val type: Int = (it["type"] as? Double ?: 0.0).toInt()
            return when (type) {
                Notification.FOLLOWING_TYPE -> parseFollowingNotif(it)
                Notification.REACTION_TYPE -> parseReactionNotif(it)
                Notification.COMMENT_TYPE -> parseCommentNotif(it)
                Notification.AWARd_TYPE -> parseAwardNotif(it)
                Notification.MENTION_TYPE -> parseMentionNotif(it)
                else -> parseGeneralNotif(it)
            }
        }

        fun parseNotifString(it: Map<String, String>): Notification {
            val type: Int = (it["type"] ?: "0.0").toDouble().toInt()
            return when (type) {
                Notification.FOLLOWING_TYPE -> parseFollowingNotifString(it)
                Notification.REACTION_TYPE -> parseReactionNotifString(it)
                Notification.COMMENT_TYPE -> parseCommentNotifString(it)
                Notification.AWARd_TYPE -> parseAwardNotifString(it)
                Notification.MENTION_TYPE -> parseMentionNotifString(it)
                else -> parseGeneralNotifString(it)
            }
        }

        private fun parseGeneralNotif(it: Map<String, Any>): Notification {
            return Notification(0,
                    it["nid"] as String? ?: "",
                    it["title"] as String? ?: "",
                    it["message"] as String? ?: "",
                    it["date"] as Long? ?: 0L,
                    it["seen"] as Boolean? ?: false)
        }

        private fun parseGeneralNotifString(it: Map<String, String>): Notification {
            return Notification(0,
                    it["nid"] ?: "",
                    it["title"] ?: "",
                    it["message"] ?: "",
                    (it["date"] ?: "0").toLong(),
                    (it["seen"] ?: "false").toBoolean())
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
                    (it["ratio"] as? Double?) ?: 1.0,
                    (it["date"] as Double).toLong(),
                    it["seen"] as Boolean
            )
        }

        private fun parseCommentNotifString(it: Map<String, String>): CommentNotification {
            return CommentNotification(
                    it["nid"] ?: "",
                    it["name"] ?: "",
                    it["pic"] ?: "",
                    it["uid"] ?: "",
                    it["mid"] ?: "",
                    it["img_url"] ?: "",
                    Meme.MemeType.of(it["mtype"] ?: "" ?: "image"),
                    it["comment"] ?: "",
                    (it["ratio"] ?: "1.0").toDouble(),
                    (it["date"] ?: "0").toLong(),
                    (it["seen"] ?: "false").toBoolean()
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
                    (it["ratio"] as? Double?) ?: 1.0,
                    (it["reaction"] as Double).toInt(),
                    (it["date"] as Double).toLong(),
                    it["seen"] as Boolean
            )
        }

        private fun parseReactionNotifString(it: Map<String, String>): ReactionNotification {
            return ReactionNotification(
                    it["nid"] ?: "",
                    it["name"] ?: "",
                    it["pic"] ?: "",
                    it["uid"] ?: "",
                    it["mid"] ?: "",
                    it["img_url"] ?: "",
                    Meme.MemeType.of(it["mtype"] ?: "" ?: "image"),
                    (it["ratio"] ?: "1.0").toDouble(),
                    (it["reaction"] ?: "0").toInt(),
                    (it["date"] ?: "0").toLong(),
                    (it["seen"] ?: "false").toBoolean()
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

        private fun parseFollowingNotifString(it: Map<String, String>): FollowingNotification {
            return FollowingNotification(
                    it["nid"] ?: "",
                    it["name"] ?: "",
                    it["pic"] ?: "",
                    it["uid"] ?: "",
                    (it["date"] ?: "0").toLong(),
                    (it["seen"] ?: "false").toBoolean())
        }

        private fun parseAwardNotif(it: Map<String, Any>): AwardNotification {
            val badge = Badge.ofID((it["awardId"] as? String?) ?: "none")
            return AwardNotification(
                    it["nid"] as String? ?: "",
                    badge,
                    (it["date"] as Double).toLong(),
                    it["seen"] as Boolean)
        }

        private fun parseAwardNotifString(it: Map<String, String>): AwardNotification {
            val badge = Badge.ofID(it["awardId"]!!)
            return AwardNotification(
                    it["nid"] ?: "",
                    badge,
                    (it["date"] ?: "0").toLong(),
                    (it["seen"] ?: "false").toBoolean())
        }

        private fun parseMentionNotif(it: Map<String, Any>): MentionNotification {
            return MentionNotification(
                    it["nid"] as String? ?: "",
                    it["name"] as String? ?: "",
                    it["pic"] as String? ?: "",
                    it["uid"] as String,
                    it["mid"] as String,
                    (it["date"] as Double).toLong(),
                    it["seen"] as Boolean
            )
        }

        private fun parseMentionNotifString(it: Map<String, String>): MentionNotification {
            return MentionNotification(
                    it["nid"] ?: "",
                    it["name"] ?: "",
                    it["pic"] ?: "",
                    it["uid"] ?: "",
                    it["mid"] ?: "",
                    (it["date"] ?: "0").toLong(),
                    (it["seen"] ?: "false").toBoolean()
            )
        }
    }
}

class FollowingNotification(id: String,
                            val followerName: String,
                            val followerPic: String,
                            val followerId: String,
                            date: Long,
                            seen: Boolean = false) : Notification
(FOLLOWING_TYPE, id, "$followerName started following you", "", date, seen)

class ReactionNotification(id: String,
                           val reactorName: String,
                           val reactorPic: String,
                           val reactorId: String,
                           val memeId: String,
                           val memePic: String,
                           val memeType: Meme.MemeType,
                           val memeRatio: Double,
                           val reactionType: Int,
                           date: Long,
                           seen: Boolean = false) : Notification
(REACTION_TYPE, id, "$reactorName reacted to your meme", "", date, seen) {

    fun getMeme(): Meme =
            Meme(id = memeId, imageId = memePic, type = memeType.name, imageRatio = memeRatio)

    fun getReaction(): Reaction =
            Reaction.ReactionType.values()[reactionType].create(memeId)

}

class CommentNotification(id: String,
                          val commenterName: String,
                          val commenterPic: String,
                          val commentorId: String,
                          val memeId: String,
                          val memePic: String,
                          val memeType: Meme.MemeType,
                          val comment: String,
                          val memeRatio: Double,
                          date: Long,
                          seen: Boolean = false) : Notification
(COMMENT_TYPE, id, "$commenterName commented on your meme", comment, date, seen) {

    fun getMeme(): Meme =
            Meme(id = memeId, imageId = memePic, type = memeType.name, imageRatio = memeRatio)

}

class MentionNotification(id: String,
                          val mentionerName: String,
                          val mentionerPic: String,
                          val mentionerId: String,
                          val memeId: String,
                          date: Long,
                          seen: Boolean = false) : Notification
(MENTION_TYPE, id, "$mentionerName mentioned you on a post", "", date, seen)

class AwardNotification(id: String,
                        val badge: Badge,
                        date: Long,
                        seen: Boolean = false) : Notification
(AWARd_TYPE, id, "Congratulations!, You have achieved the ${badge.label} badge", badge.description, date, seen)