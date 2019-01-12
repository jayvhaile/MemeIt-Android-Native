package com.innov8.memeit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.preference.PreferenceManager
import androidx.multidex.MultiDexApplication
import com.cloudinary.android.MediaManager
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.innov8.memeit.commons.models.TypefaceManager
import com.innov8.memeit.commons.notidChannelName
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.MyUser
import com.memeit.backend.models.SignInMethod
import io.fabric.sdk.android.Fabric


class MemeItApp : MultiDexApplication() {
    companion object {
        lateinit var instance: Application
        private const val apiVersion = 1
        const val SERVER_DOMAIN = "https://memeitapp.com"
        const val SERVER_URL = "$SERVER_DOMAIN/api/v$apiVersion/"
        const val STORAGE_URL = "https://storage.googleapis.com/meme-store/"

        private const val LOCAL_SERVER_URL = "http://127.0.0.1:8080/api/v$apiVersion/"
        private const val STRICT_MODE = false
        const val USE_LOCAL_SERVER = false
        const val FACEBOOK_AD_PLACEMENT_ID = "262717847755102_262724637754423"
//        const val FACEBOOK_AD_PLACEMENT_ID="126436314944426_126436834944374"

    }

    override fun onCreate() {
        Fabric.with(this, Crashlytics())
        if (STRICT_MODE) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectCustomSlowCalls()
                    .penaltyFlashScreen()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
        super.onCreate()
        instance = this
        MemeItClient.init(applicationContext, if (USE_LOCAL_SERVER) LOCAL_SERVER_URL else SERVER_URL)
        FirebaseApp.initializeApp(this)
        val config = mapOf(
                "cloud_name" to "innov8",
                "api_key" to "591249199742556",
                "api_secret" to "yT2mxv0vQrEWjzsPrmyD6xu5a-Y"
        )
        MediaManager.init(this, config)
        Fresco.initialize(this, ImagePipelineConfig.newBuilder(this)
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build())
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        initNotificationChannel()
        initUser()
        TypefaceManager.init(this)
        FirebaseMessaging.getInstance().subscribeToTopic("general")
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    notidChannelName,
                    notidChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "A channel which shows notification about MemeIt Events"
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(channel)
        }
    }

    private fun initUser() {
        if (MemeItClient.myUser != null) return
        MyUser.save(PreferenceManager.getDefaultSharedPreferences(this),
                "123",
                SignInMethod.USERNAME,
                "1234",
                "jayv",
                "Jv",
                "!",
                "aaa")
    }
}