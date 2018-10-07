package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.NotificationAdapter
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.*
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    lateinit var notificationAdapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationAdapter = NotificationAdapter(this)
        notif_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        notif_list.adapter = notificationAdapter
        swipe_refresh.setOnRefreshListener {
            load()
        }

        setSupportActionBar(notf_toolbar)
        supportActionBar?.title = "Notifications"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        load()
    }

    fun load() {
        MemeItUsers.getMyNotifications(0, 300).call({ t ->
            if (t.isEmpty()) {
                notificationAdapter.clear()
                return@call
            }
            val notifs = t.map {
                parseNotif(it)
            }.toList()
            notificationAdapter.setAll(notifs)
            MemeItUsers.markNotificationSeen()
        },{

        })

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
