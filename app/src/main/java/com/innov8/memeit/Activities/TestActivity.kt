package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.R
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        post.setOnClickListener {
            val url = url_edit_text.text.toString()

            status.text = "posting"

            MemeItMemes.postMeme(Meme(imageId = url,
                    imageRatio = 1.0,
                    type = Meme.MemeType.GIF.name,
                    description = "gif test meme",
                    tags = mutableListOf("#test"))).call({ meme ->

                status.text = "posted"


            }) { error ->
                status.text = error


            }
        }
    }
}
