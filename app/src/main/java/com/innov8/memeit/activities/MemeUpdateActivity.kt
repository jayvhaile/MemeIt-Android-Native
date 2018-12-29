package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme
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
                updateMeme(description.text.toString())
                true
            }
            else -> false
        }
    }

    private fun init() {
        description.text?.append(meme.description)
        meme_image_view.loadMeme(meme)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Meme"

        btn_add_tag.setOnClickListener {
            startActivityForResult(Intent(this, SearchTagActivity::class.java), SearchTagActivity.REQUEST_CODE)
        }
        btn_mention_user.setOnClickListener {
            startActivityForResult(Intent(this, SearchUserActivity::class.java), SearchUserActivity.REQUEST_CODE)
        }
    }

    private fun updateMeme(desc: String? = meme.description) {
        val p = MaterialDialog.Builder(this)
                .title("Updating Meme")
                .progress(true, 100)
                .build()
        p.show()
        MemeItMemes.updateMeme(Meme(id = meme.id, description = desc)).call({
            p.dismiss()
            toast("Meme Updated Successfully")
            finish()
        }) {
            p.dismiss()
            toast(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SearchTagActivity.REQUEST_CODE -> {
                if (resultCode == SearchTagActivity.RESULT_CODE_SELECTED) {
                    description.append(" #")
                    description.append(data?.getStringExtra(SearchTagActivity.PARAM_SELECTED_TAG))
                }
            }
            SearchUserActivity.REQUEST_CODE -> {
                if (resultCode == SearchUserActivity.RESULT_CODE_SELECTED) {
                    description.append(" @")
                    description.append(data?.getStringExtra(SearchUserActivity.PARAM_SELECTED_USERNAME))
                }
            }
        }
    }
}

