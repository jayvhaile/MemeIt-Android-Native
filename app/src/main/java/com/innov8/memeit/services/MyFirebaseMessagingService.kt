package com.innov8.memeit.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.innov8.memeit.activities.NotificationActivity
import com.innov8.memeit.activities.SettingsActivity
import com.memeit.backend.MemeItClient.context
import com.innov8.memeit.commons.*
import com.innov8.memeit.R
import com.innov8.memeit.activities.BadgeAwardDialogActivity
import com.innov8.memeit.workers.uploadFirebaseToken
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.myUser
import com.memeit.backend.buildGson
import com.memeit.backend.models.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        MyUser.get(PreferenceManager.getDefaultSharedPreferences(context)) ?: return
        uploadFirebaseToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val pushNotification = PushNotification.parse(remoteMessage.data)
        if (!pushNotification.isForMe()) return
        if (pushNotification.type == Notification.AWARd_TYPE) {
            startActivity(Intent(context, BadgeAwardDialogActivity::class.java).apply {
                putExtra("badge", pushNotification.badge())
            })
        }
        if (pushNotification.isEnabled(this)) sendUserNotification(pushNotification)

    }

    private fun PushNotification.isForMe(): Boolean {
        val me = myUser
        return notifiedUserId == null || (me != null && notifiedUserId == me.id)
    }

    private fun PushNotification.badge() = awardID?.let { Badge.ofID(it) }

    private fun PushNotification.isEnabled(context: Context): Boolean {
        return when (type) {
            Notification.FOLLOWING_TYPE -> SettingsActivity.isFollowedNotifEnabled(context)
            Notification.REACTION_TYPE -> SettingsActivity.isReactionNotifEnabled(context)
            Notification.COMMENT_TYPE, Notification.COMMENT_MENTION_TYPE, Notification.COMMENT_REPLY_TYPE ->
                SettingsActivity.isCommentNotifEnabled(context)
            else -> SettingsActivity.isNotifEnabled(context)
        }
    }


}
