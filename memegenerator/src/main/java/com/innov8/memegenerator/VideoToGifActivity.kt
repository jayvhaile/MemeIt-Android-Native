package com.innov8.memegenerator

import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_video_to_gif.*
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream

class VideoToGifActivity : AppCompatActivity() {

    val retriever by lazy { MediaMetadataRetriever() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_to_gif)


        val file = File(intent?.getStringExtra("gif"))
        if (file.exists()) {
            val fis = FileInputStream(file)

            retriever.setDataSource(fis.fd)


            imageView.setImageBitmap(retriever.getFrameAtTime(0))
        }

    }
}
