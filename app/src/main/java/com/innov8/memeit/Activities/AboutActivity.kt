package com.innov8.memeit.Activities

import androidx.appcompat.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.innov8.memeit.BuildConfig

import com.innov8.memeit.R

class AboutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aboutPage = AboutPage(this)
                .addEmail("innovapp.ethio@gmail.com")
                .addFacebook("")
                .addWebsite("www.memeitapp.com")
                .setImage(R.mipmap.icon)
                .setDescription("MemeIt\nVersion ${BuildConfig.VERSION_NAME}\nBy Innov8 Apps")
                .addItem(Element().setIconDrawable(R.drawable.ic_telegram).setTitle("Telegram").setIntent(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/D8nyzkIBl9N2YWJnsF1w-A"))))
                .create()

        setContentView(aboutPage)
    }


}
