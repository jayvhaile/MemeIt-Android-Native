package com.innov8.memeit.Loaders

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
                        "Bez",
                        "a",
                        "a",
                        System.currentTimeMillis(),
                        false
                ),
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
                CommentNotification(
                        "asd",
                        "Tenu",
                        "aa",
                        "afdd",
                        "dsdf",
                        "sdf",
                        Meme.MemeType.IMAGE,
                        "wow funny",
                        1.0,
                        System.currentTimeMillis(),
                        false
                ),
                AwardNotification(
                        "asd",
                        Badge.ofID("first_001"),
                        System.currentTimeMillis(),
                        false
                ),
                MentionNotification(
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

        ))

    }
}