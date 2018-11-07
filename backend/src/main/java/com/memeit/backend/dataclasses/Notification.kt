package com.memeit.backend.dataclasses


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

        fun parseNotif(it: Map<String, Any>): Notification {
            val type: Int = (it["type"] as? Double ?: 0.0).toInt()
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
                    it["date"] as Long? ?:0L,
                    it["seen"] as Boolean??:false)
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
}

class FollowingNotification(id: String,
                            val followerName: String,
                            val followerPic: String,
                            val followerId: String,
                            date: Long,
                            seen: Boolean = false) : Notification
(1, id, "$followerName started following you", "", date, seen)

class ReactionNotification(id: String,
                           val reactorName: String,
                           val reactorPic: String,
                           val reactorId: String,
                           val memeId: String,
                           val memePic: String,
                           val memeType: Meme.MemeType,
                           val reactionType: Int,
                           date: Long,
                           seen: Boolean = false) : Notification
(2, id, "$reactorName reacted to your meme", "", date, seen) {

    fun getMeme(): Meme =
            Meme(id = memeId, imageId = memePic, type = memeType.name)

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
                          date: Long,
                          seen: Boolean = false) : Notification
(3, id, "$commenterName commented on your meme", comment, date, seen) {

    fun getMeme(): Meme =
            Meme(id = memeId, imageId = memePic, type = memeType.name)

}

class AwardNotification(id: String,
                        val awardType: Int,
                        date: Long,
                        seen: Boolean = false) : Notification
(4, id, "You are awarded...shit", "", date, seen)