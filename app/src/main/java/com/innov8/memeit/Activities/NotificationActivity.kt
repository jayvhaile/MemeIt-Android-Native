package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.NotificationAdapter
import com.innov8.memeit.R
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.CommentNotification
import com.memeit.backend.dataclasses.FollowingNotification
import com.memeit.backend.dataclasses.Notification
import com.memeit.backend.dataclasses.ReactionNotification
import com.memeit.backend.utilis.OnCompleteListener
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
        supportActionBar?.title="Notifications"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        load()
    }

    fun load() {
        MemeItUsers.getInstance().getNotificationList(0, 300, object : OnCompleteListener<MutableList<MutableMap<String, Any>>?> {
            override fun onSuccess(t: MutableList<MutableMap<String, Any>>?) {
                if (t == null) {
                    notificationAdapter.clear()
                    return
                }/*
                AsyncLoader<List<Notification>>{

                }*/

                val notifs=t.map {
                    val type: Int = (it["type"] as Double).toInt()
                    when (type) {
                        Notification.FOLLOWING_TYPE -> {
                            FollowingNotification(
                                    it["nid"] as String? ?: "",
                                    it["name"] as String? ?: "",
                                    it["pic"] as String? ?: "",
                                    it["uid"] as String,
                                    (it["date"] as Double).toLong(),
                                    it["seen"] as Boolean)
                        }
                        Notification.REACTION_TYPE -> {
                            ReactionNotification(
                                    it["nid"] as String? ?: "",
                                    it["name"] as String? ?: "",
                                    it["pic"] as String? ?: "",
                                    it["uid"] as String,
                                    it["mid"] as String,
                                    it["img_url"] as String,
                                    (it["reaction"] as Double).toInt(),
                                    (it["date"] as Double).toLong(),
                                    it["seen"] as Boolean
                            )
                        }
                        Notification.COMMENT_TYPE -> {
                            CommentNotification(
                                    it["nid"] as String? ?: "",
                                    it["name"] as String? ?: "",
                                    it["pic"] as String? ?: "",
                                    it["uid"] as String,
                                    it["mid"] as String,
                                    it["img_url"] as String,
                                    it["comment"] as String,
                                    (it["date"] as Double).toLong(),
                                    it["seen"] as Boolean
                            )
                        }

                        else-> {
                            Notification(0,
                                    it["nid"] as String? ?: "",
                                    it["title"] as String? ?: "",
                                    it["message"] as String? ?: "",
                                    it["date"] as Long,
                                    it["seen"] as Boolean)
                        }
                    }
                }.toList()
                notificationAdapter.setAll(notifs)
                MemeItUsers.getInstance().markAllNotificationSeen(null)
            }

            override fun onFailure(error: OnCompleteListener.Error?) {

            }
        })

    }
}
