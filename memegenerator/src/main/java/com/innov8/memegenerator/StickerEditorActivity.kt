package com.innov8.memegenerator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.commons.loadBitmapfromStream
import kotlinx.android.synthetic.main.activity_sticker_editor.*
import java.io.File
import java.io.FileInputStream

class StickerEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_editor)
        val url = intent?.getStringExtra("url") ?: throw IllegalStateException("url is required")
        bitmapEraserView.bitmap = loadBitmapfromStream(FileInputStream(File(url)))

    }

    companion object {
        fun startWithBitmapUri(context: Context, url: String) {
            context.startActivity(Intent(context, StickerEditorActivity::class.java).apply {
                putExtra("url", url)
            })
        }

    }
}
