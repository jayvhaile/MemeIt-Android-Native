package com.innov8.memeit.Services

import android.content.Intent
import android.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.innov8.memeit.Activities.NotificationActivity
import com.innov8.memeit.Activities.SettingsActivity
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.MyUser
import com.memeit.backend.models.Notification
import com.innov8.memeit.commons.*
import com.innov8.memeit.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        MemeItUsers.updateUserToken(s).call { }
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
        if (enabled) sendUserNotification(NotifData(n.title, n.message,R.mipmap.icon, Intent(context, NotificationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }), 542)
    }


}
