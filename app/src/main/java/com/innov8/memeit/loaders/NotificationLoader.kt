package com.innov8.memeit.loaders

import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.*

class MyNotificationLoader : Loader<Notification> {
    override var skip: Int = 0


    override fun load(limit: Int, onSuccess: (List<Notification>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getMyNotifications(skip, limit).call({
            onSuccess(it.map { n -> Notification.parseNotif(n) })
        }, onError)
    }
}

class TestNotificationLoader : Loader<Notification> {
    override var skip: Int = 0


    override fun load(limit: Int, onSuccess: (List<Notification>) -> Unit, onError: (String) -> Unit) {
        onSuccess(listOf(
                FollowingNotification(
                        "45",
                        "Nathaniel",
                        "a",
                        "a",
                        System.currentTimeMillis() - 1000 * 60 * 29,
                        false
                ),
                CommentNotification(
                        "asd",
                        "Mikiyas",
                        "aa",
                        "afdd",
                        "dsdf",
                        "sdf",
                        Meme.MemeType.IMAGE,
                        "loll thats one funny meme, @jv you r killing it",
                        1.0,
                        System.currentTimeMillis(),
                        false
                ),
                ReactionNotification(
                        "asd",
                        "Biruk",
                        "aa",
                        "afdd",
                        "dsdf",
                        "sdf",
                        Meme.MemeType.IMAGE,
                        1.0,
                        0,
                        System.currentTimeMillis(),
                        false
                ),
                Notification(0,
                        "asd",
                        "An update is available",
                        "Click here to update",
                        System.currentTimeMillis(),
                        false),
                MemeMentionNotification("a", "Jayv",
                        "",
                        "",
                        "",
                        System.currentTimeMillis(),
                        false
                ),
                CommentMentionNotification("a", "Jayv",
                        "",
                        "",
                        "",
                        "@biruk you got to look at this",
                        System.currentTimeMillis(),
                        false
                ),
                AwardNotification(
                        "asd",
                        Badge.ofID("first_001"),
                        System.currentTimeMillis(),
                        false
                )


        )
                /*,
                ReactionNotification(
                        "asd",
                        "Jv",
                        "aa",
                        "afdd",
                        "dsdf",
                        "sdf",
                        Meme.MemeType.IMAGE,
                        1.0,
                        0,
                        System.currentTimeMillis(),
                        false
                ),
                AwardNotification(
                        "asd",
                        Badge.ofID("first_001"),
                        System.currentTimeMillis(),
                        false
                ),
                MemeMentionNotification(
                        "asf",
                        "Biruk",
                        "afdf",
                        "asf",
                        "sfd",
                        System.currentTimeMillis(),
                        false
                ),
                Notification(0,
                        "asd",
                        "An update is available",
                        "Click here to update",
                        System.currentTimeMillis(),
                        false)

        )*/)

    }
}

operator fun <T> List<T>.times(int: Int): List<T> {
    return mutableListOf<T>().apply {
        for (i in 0 until int) addAll(this)
    }.shuffled()
}