package com.innov8.memeit.Services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.innov8.memeit.Activities.NotificationActivity
import com.innov8.memeit.Activities.SettingsActivity
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.MyUser
import com.memeit.backend.dataclasses.Notification

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
        if (enabled) sendUserNotification(NotifData(n.title, n.message), 542)
    }


    private fun sendUserNotification(data: NotifData, notifyID: Int) {

        val pendingIntent = PendingIntent.getActivity(context, 0, data.intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, MemeItApp.notidChannelName)
        } else {
            android.app.Notification.Builder(context)
        }
        builder.setContentTitle(data.title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(android.app.Notification.BigTextStyle().bigText(data.message))
                .setContentText(data.message)
                .setSmallIcon(data.icon)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(notifyID, builder.build())
    }

    private data class NotifData(val title: String,
                                 val message: String,
                                 val icon: Int = R.mipmap.icon,
                                 val intent: Intent = Intent(context, NotificationActivity::class.java).apply {
                                     addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                 })
}
