package com.innov8.memeit.Services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.text.TextUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.innov8.memeit.Activities.MainActivity
import com.innov8.memeit.R
import com.innov8.memeit.log
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        MemeItUsers.updateUserToken(s).call { log("fuckyeah", s) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        sendUserNotification(NotifData(remoteMessage.data["title"]?:"error","hi"), 351)
    }


    private fun sendUserNotification(data: NotifData, notifyID: Int) {

        val pendingIntent = PendingIntent.getActivity(context, 0, data.intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = Notification.Builder(context)
        builder.setContentTitle(data.title)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(data.sound)
                .setContentIntent(pendingIntent)
                .setStyle(Notification.BigTextStyle().bigText(data.message))
                .setContentText(data.message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(data.icon)
        if (!TextUtils.isEmpty(data.ticker)) builder.setTicker(data.ticker)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(notifyID, builder.build())
    }

    private data class NotifData(val title: String,
                                 val message: String,
                                 val icon: Int = R.mipmap.icon,
                                 val intent: Intent = Intent(context, MainActivity::class.java).apply {
                                     addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                 },
                                 val ticker: String? = null,
                                 val sound: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
}
