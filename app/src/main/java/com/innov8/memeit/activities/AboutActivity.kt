package com.innov8.memeit.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.innov8.memeit.commons.SuperActivity
import com.innov8.memeit.BuildConfig
import com.innov8.memeit.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutActivity : SuperActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aboutPage = AboutPage(this)
                .addEmail("innovapp.ethio@gmail.com")
                .addFacebook("")
                .addWebsite("www.memeitapp.com")
                .setImage(R.mipmap.icon)
                .setDescription("MemeIt\nVersion ${BuildConfig.VERSION_NAME}\nBy Innov8 Apps")
                .addItem(Element().setTitle("Telegram").setIntent(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/D8nyzkIBl9N2YWJnsF1w-A"))))
                .create()

        setContentView(aboutPage)
    }


}
