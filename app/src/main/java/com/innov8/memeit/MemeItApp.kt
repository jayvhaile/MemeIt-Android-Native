package com.innov8.memeit

import android.app.Application
import android.os.StrictMode
import android.preference.PreferenceManager
import androidx.multidex.MultiDexApplication
import com.cloudinary.android.MediaManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.google.firebase.FirebaseApp
import com.memeit.backend.MemeItClient


class MemeItApp : MultiDexApplication() {
    companion object {
        lateinit var instance: Application
        const val apiVersion = 1
        const val SERVER_URL = "https://safe-beyond-33046.herokuapp.com/api/$apiVersion/"
        private const val LOCAL_SERVER_URL = "http://127.0.0.1:5000/api/$apiVersion/"
        private const val STRICT_MODE = false
        private const val USE_LOCAL_SERVER = false

    }

    override fun onCreate() {
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
        /*MUser.save(PreferenceManager.getDefaultSharedPreferences(this),
                "123",
                SignInMethod.USERNAME,
                "1234",
                "jayv",
                "Jv",
                "aaa",
                "aaa")*/
    }
}