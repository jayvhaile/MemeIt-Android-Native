package com.innov8.memeit.Activities

import android.os.Bundle
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
    private var uid: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        followedTagsAdapter = TagsAdapter(this)

        val loader={
            load()
        }
        uid = intent?.getStringExtra("uid")
        if(uid==null){
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
        }else{
            setContentView(R.layout.activity_tags_user)
            followed_tags_users.layoutManager=StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
            followed_tags_users.adapter=followedTagsAdapter
            setSupportActionBar(tags_toolbar_user)
            tags_swipe_refresh_user.setOnRefreshListener(loader)
        }
        supportActionBar?.title="Tags"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loader()
    }
    private fun load(){
        if(uid==null){
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
        }else{
            MemeItUsers.getInstance().getFollowingTagsFor(uid,0,1000,Listener<List<Tag>>({
                followedTagsAdapter.setAll(it)
                tags_swipe_refresh_user.isRefreshing=false
            },{}))

        }

    }
}