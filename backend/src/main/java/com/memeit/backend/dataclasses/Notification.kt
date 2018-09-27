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
            Meme(memeId, memePic, memeType)
    fun getReaction():Reaction=
            Reaction.create(Reaction.ReactionType.values()[reactionType],memeId)

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
            Meme(memeId, memePic, memeType)

}

class AwardNotification(id: String,
                        val awardType: Int,
                        date: Long,
                        seen: Boolean = false) : Notification
(4, id, "You are awarded...shit", "", date, seen)