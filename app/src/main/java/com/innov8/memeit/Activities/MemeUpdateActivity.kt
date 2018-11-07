package com.innov8.memeit.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast
import com.innov8.memeit.loadImage
import com.innov8.memeit.loadMeme
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme
import kotlinx.android.synthetic.main.activity_meme_update.*

class MemeUpdateActivity : AppCompatActivity() {

    companion object {
        const val PARAM_MEME = "meme"
        fun startWithMeme(context: Context, meme: Meme) {
            context.startActivity(Intent(context, MemeUpdateActivity::class.java).apply {
                putExtra(PARAM_MEME, meme)
            })
        }

    }

    lateinit var meme: Meme
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_update)
        val m = intent?.getParcelableExtra<Meme>(PARAM_MEME)
        if (m == null) {
            finish()
            return
        }
        meme = m
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_meme_update, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_save -> {
                updateMeme(tags, caption_field.text.toString())
                true
            }
            else -> false
        }
    }

    private fun init() {
        poster_pp.loadImage(meme.poster?.profileUrl)
        caption_field.text?.append(meme.description)
        meme_image_view.loadMeme(meme)
        tags_field.text?.append(meme.tags.map { "#$it" }.joinToString(" "))


        setSupportActionBar(toolbar)
        supportActionBar?.title="Edit Meme"
    }

    private fun updateMeme(tags: List<String> = meme.tags, desc: String? = meme.description) {
        val p = MaterialDialog.Builder(this)
                .title("Updating Meme")
                .progress(true, 100)
                .build()
        p.show()
        MemeItMemes.updateMeme(Meme(id = meme.id, tags = tags.toMutableList(), description = desc)).call({
            p.dismiss()
            toast("Meme Updated Successfully")
            finish()
        }) {
            p.dismiss()
            toast(it)
        }
    }

    private val tags
        get() = tags_field.text.split(" ")
                .asSequence()
                .filter { it.startsWith("#") && it.length > 1 }
                .map { it.substring(1) }
                .toList()
}

