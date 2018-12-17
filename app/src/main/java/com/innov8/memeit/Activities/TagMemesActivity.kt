package com.innov8.memeit.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Fragments.MemeListFragment
import com.innov8.memeit.Loaders.TagMemeLoader
import com.innov8.memeit.R
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.activity_tag_memes.*

class TagMemesActivity : AppCompatActivity() {
    private val tag: String by lazy { intent.getStringExtra(PARAM_TAG) }
    private var isFollowed = false
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_memes)
        supportFragmentManager.beginTransaction()
                .add(R.id.holder, MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER_FOR_TAG, TagMemeLoader(tag)))
                .commit()
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "#$tag"
            setDisplayHomeAsUpEnabled(true)
        }
        //todo: check if tag is followed
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tag_follow_menu, menu)
        menu.getItem(0).title = if (isFollowed) "Unfollow" else "Follow"
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Follow") {
            MemeItUsers.followTags(arrayOf(tag)).call {
                isFollowed = true
                invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val PARAM_TAG = "tag"

        fun startWithTag(context: Context, tag: String) {
            context.startActivity(Intent(context, TagMemesActivity::class.java).apply {
                putExtra(PARAM_TAG, tag)
            })
        }
    }
}
