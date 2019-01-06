package com.innov8.memeit.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.adapters.TagsAdapter
import com.innov8.memeit.loaders.*
import com.innov8.memeit.R
import com.innov8.memeit.commons.ELEAdapter
import com.innov8.memeit.commons.LoaderAdapterHandler
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag
import com.memeit.backend.models.User
import kotlinx.android.synthetic.main.activity_tags.*
import kotlinx.android.synthetic.main.activity_user_tag.*
import kotlinx.android.synthetic.main.fragment_meme_list.*

class TagsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)
        val pager = Pager(supportFragmentManager)
        tags_pager.adapter = pager
        tags_tab.setupWithViewPager(tags_pager)
    }

    class Pager(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val frags = listOf("Followed Tags" to TagFragment.newInstance(MyTagLoader()),
                "Popular Tags" to TagFragment.newInstance(PopularTagLoader()),
                "Trending Tags" to TagFragment.newInstance(TrendingTagLoader()))

        override fun getItem(position: Int): Fragment = frags[position].second
        override fun getCount(): Int = frags.size
        override fun getPageTitle(position: Int): CharSequence = frags[position].first
    }
}

class UserTagActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = intent?.getParcelableExtra<User>("user")
        if (user == null) {
            finish()
            return
        }
        setContentView(R.layout.activity_user_tag)
        setSupportActionBar(tags_toolbar)
        supportActionBar?.title = "Tags Followed by ${user.name}"
        supportFragmentManager.beginTransaction()
                .replace(R.id.holder, TagFragment.newInstance(UserTagLoader(user.uid!!)))
                .commit()
    }
}

class TagsChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_tag)
        setSupportActionBar(tags_toolbar)
        supportActionBar?.title = "Choose Tags"
        supportFragmentManager.beginTransaction()
                .replace(R.id.holder, TagFragment.newInstance(PopularTagLoader()))
                .commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tags_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tag_done -> {
                MainActivity.start(this) {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

class TagFragment : Fragment() {
    private lateinit var tagLoader: TagLoader

    companion object {
        fun newInstance(loader: TagLoader): TagFragment {
            val fragment = TagFragment()
            val arg = Bundle()
            arg.putParcelable("loader", loader)
            fragment.arguments = arg
            return fragment
        }
    }

    private lateinit var tagsAdapter: TagsAdapter
    private lateinit var loaderAdapter: LoaderAdapterHandler<Tag>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tagLoader = arguments?.getParcelable("loader") ?: return
        tagsAdapter = TagsAdapter(context!!)
        loaderAdapter = LoaderAdapterHandler(tagsAdapter, tagLoader)
        loaderAdapter.load()
        loaderAdapter.onLoaded = { swipe_to_refresh?.isRefreshing = false }
        loaderAdapter.onLoadFailed = { message ->
            view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
            swipe_to_refresh?.isRefreshing = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_recycler_view.adapter = tagsAdapter
        val lm = GridLayoutManager(context!!, 2, RecyclerView.VERTICAL, false)
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (tagsAdapter.getItemViewType(position)) {
                    ELEAdapter.TYPE_EMPTY, ELEAdapter.TYPE_ERROR, ELEAdapter.TYPE_LOADING, ELEAdapter.TYPE_LOAD_MORE -> 2
                    else -> 1
                }
            }
        }
        meme_recycler_view.layoutManager = lm
        swipe_to_refresh.setOnRefreshListener {
            loaderAdapter.refresh()
        }
    }

}

