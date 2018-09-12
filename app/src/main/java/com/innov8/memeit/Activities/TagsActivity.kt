package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memeit.Adapters.TagsAdapter
import com.innov8.memeit.R
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.Tag
import com.memeit.backend.utilis.Listener
import kotlinx.android.synthetic.main.activity_tags.*

class TagsActivity : AppCompatActivity() {

    lateinit var followedTagsAdapter: TagsAdapter
    lateinit var popularTagsAdapter: TagsAdapter
    lateinit var trendingTagsAdapter: TagsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)
        followedTagsAdapter = TagsAdapter(this)
        popularTagsAdapter = TagsAdapter(this)
        trendingTagsAdapter = TagsAdapter(this)

        followed_tags.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        popular_tags.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        trending_tags.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        followed_tags.adapter = followedTagsAdapter
        popular_tags.adapter = popularTagsAdapter
        trending_tags.adapter = trendingTagsAdapter

        load()
        tags_swipe_refresh.setOnRefreshListener {
            load()
        }
        setSupportActionBar(tags_toolbar)
        supportActionBar?.title="Tags"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun load(){
        MemeItUsers.getInstance().getFollowingTags(0,1000,Listener<List<Tag>>({
            followedTagsAdapter.setAll(it)
            tags_swipe_refresh.isRefreshing=false
        },{}))
        MemeItMemes.getInstance().getPopularTags(null,0,100,Listener<List<Tag>>({
            popularTagsAdapter.setAll(it)
            tags_swipe_refresh.isRefreshing=false
        },{}))
        MemeItMemes.getInstance().getTrendingTags(0,100,Listener<List<Tag>>({
            trendingTagsAdapter.setAll(it)
            tags_swipe_refresh.isRefreshing=false
        },{}))
    }
}