package com.innov8.memeit.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.innov8.memeit.Adapters.TagsAdapter
import com.innov8.memeit.R
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.Tag
import com.memeit.backend.utilis.Listener
import kotlinx.android.synthetic.main.activity_tags.*
import kotlinx.android.synthetic.main.activity_tags_user.*

class TagsActivity : AppCompatActivity() {

    private lateinit var followedTagsAdapter: TagsAdapter
    private lateinit var popularTagsAdapter: TagsAdapter
    private lateinit var trendingTagsAdapter: TagsAdapter
    private var uid: String? = null
    private var choose: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        followedTagsAdapter = TagsAdapter(this)

        val loader = {
            load()
        }
        choose = intent?.getBooleanExtra("choose", false) ?: false
        uid = intent?.getStringExtra("uid")
        if (!choose && uid == null) {
            setContentView(R.layout.activity_tags)
            popularTagsAdapter = TagsAdapter(this)
            trendingTagsAdapter = TagsAdapter(this)

            followed_tags.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            popular_tags.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            trending_tags.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

            followed_tags.adapter = followedTagsAdapter
            popular_tags.adapter = popularTagsAdapter
            trending_tags.adapter = trendingTagsAdapter

            tags_swipe_refresh.setOnRefreshListener(loader)

            setSupportActionBar(tags_toolbar)
        } else {
            setContentView(R.layout.activity_tags_user)

            tag_title.text=if(choose)"Popular Tags" else "Followed Tags"
            followed_tags_users.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            followed_tags_users.adapter = followedTagsAdapter
            setSupportActionBar(tags_toolbar_user)
            tags_swipe_refresh_user.setOnRefreshListener(loader)
        }
        supportActionBar?.title = "Tags"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loader()
    }

    private fun load() {
        if (!choose&&uid == null) {
            MemeItUsers.getInstance().getFollowingTags(0, 1000, Listener<List<Tag>>({
                followedTagsAdapter.setAll(it)
                tags_swipe_refresh.isRefreshing = false
            }, {}))
            MemeItMemes.getInstance().getPopularTags(null, 0, 100, Listener<List<Tag>>({
                popularTagsAdapter.setAll(it)
                tags_swipe_refresh.isRefreshing = false
            }, {}))
            MemeItMemes.getInstance().getTrendingTags(0, 100, Listener<List<Tag>>({
                trendingTagsAdapter.setAll(it)
                tags_swipe_refresh.isRefreshing = false
            }, {}))
        } else {
            if (choose) {
                MemeItMemes.getInstance().getPopularTags(null, 0, 1000, Listener<List<Tag>>({
                    followedTagsAdapter.setAll(it)
                    tags_swipe_refresh_user.isRefreshing = false
                }, {}))
            } else {
                MemeItUsers.getInstance().getFollowingTagsFor(uid, 0, 1000, Listener<List<Tag>>({
                    followedTagsAdapter.setAll(it)
                    tags_swipe_refresh_user.isRefreshing = false
                }, {}))
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(choose)
            menuInflater.inflate(R.menu.tags_activity_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.tag_done->{
                val i = Intent(this, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}