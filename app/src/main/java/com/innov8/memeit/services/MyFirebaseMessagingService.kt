package com.innov8.memeit.services

import android.content.Intent
import android.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.innov8.memeit.activities.NotificationActivity
import com.innov8.memeit.activities.SettingsActivity
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.models.MyUser
import com.memeit.backend.models.Notification
import com.innov8.memeit.commons.*
import com.innov8.memeit.R
import com.innov8.memeit.activities.BadgeAwardDialogActivity
import com.innov8.memeit.workers.uploadFirebaseToken
import com.memeit.backend.models.AwardNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        MyUser.get(PreferenceManager.getDefaultSharedPreferences(context)) ?: return
        uploadFirebaseToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val nuid = data["nuid"]
        val mu = MyUser.get(PreferenceManager.getDefaultSharedPreferences(context)) ?: return
        if (nuid != mu.id) return
        val n = Notification.parseNotifString(data)
        val enabled = when (n.type) {
            Notification.FOLLOWING_TYPE -> SettingsActivity.isFollowedNotifEnabled(this)
            Notification.REACTION_TYPE -> SettingsActivity.isReactionNotifEnabled(this)
            Notification.COMMENT_TYPE -> SettingsActivity.isCommentNotifEnabled(this)
            else -> SettingsActivity.isNotifEnabled(this)
        }
        if (n is AwardNotification) {
            startActivity(Intent(context, BadgeAwardDialogActivity::class.java).apply {
                putExtra("badge", n.badge)
            })
        }
        if (enabled) sendUserNotification(NotifData(n.title, n.message, R.mipmap.icon, Intent(context, NotificationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }), 542)


    }


}
