package com.innov8.memeit.activities

import android.content.Intent
import com.innov8.memeit.commons.SuperActivity
import android.os.Bundle
import com.innov8.memeit.R
import com.innov8.memeit.fragments.PhotosChooserFragment

class PhotoChooserActivity : SuperActivity() {
    companion object {
        const val REQUEST_CODE = 260
        const val RESULT_CODE = 265
        const val RESULT_URL = "url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_chooser)
        supportFragmentManager.beginTransaction()
                .replace(R.id.holder, PhotosChooserFragment.newInstance(false) {
                    setResult(RESULT_CODE, Intent().apply {
                        putExtra(RESULT_URL, it)
                    })
                    finish()
                })
                .commit()
    }
}
