package com.innov8.memeit.activities

import android.os.Bundle
import com.innov8.memeit.commons.SuperActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.utils.MyFragmentPagerAdapter
import com.innov8.memeit.fragments.MemeListFragment
import com.innov8.memeit.fragments.TagSearchFragment
import com.innov8.memeit.fragments.UserSearchFragment
import com.innov8.memeit.loaders.SearchMemeLoader
import com.innov8.memeit.R
import com.innov8.memeit.utils.addOnTabSelected
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : SuperActivity() {

    private val pagerAdapter by lazy {
        SearchPagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        search_tabs.setupWithViewPager(search_pager)
        setSupportActionBar(search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        search_view.addOnTextChanged {
            searchMemes(it)
            searchUsers(it)
            searchTags(it)
        }

        search_tabs.addOnTabSelected {
            when (it.position) {
                0 -> {
                    search_view.hint = "Search Memes"
                }
                1 -> {
                    search_view.hint = "Search People"
                }
                2 -> {
                    search_view.hint = "Search Tags"
                }
            }
        }
        search_pager.adapter = pagerAdapter
        search_pager.offscreenPageLimit = 2
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    private fun searchMemes(text: String) {
        val f = pagerAdapter.getFragmentAt(0) as MemeListFragment
        (f.memeLoader as SearchMemeLoader).apply {
            search = getSearchText(text)
            tags = getSearchTags(text)
        }
        f.loaderAdapterHandler.refresh(true)
    }

    private fun getSearchText(text: String): String {
        return text.split(" ")
                .filter { !it.startsWith('#') }
                .joinToString(" ")
    }

    private fun getSearchTags(text: String): Array<String> {
        return text.split(" ")
                .filter { it.startsWith('#') && it.length > 1 }
                .map { it.substring(1) }
                .toTypedArray()
    }

    private fun searchUsers(search: String) {
        val f = pagerAdapter.getFragmentAt(1) as UserSearchFragment
        f.setFilter(search)
    }

    private fun searchTags(search: String) {
        val f = pagerAdapter.getFragmentAt(2) as TagSearchFragment
        f.setFilter(search)
    }

    inner class SearchPagerAdapter(fm: FragmentManager) : MyFragmentPagerAdapter(fm) {

        private val titles = listOf("Memes", "People", "Tags")

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, SearchMemeLoader())
                1 -> UserSearchFragment().apply {
                    onItemClicked = { ProfileActivity.startWithUser(this@SearchActivity, it) }
                }
                2 -> TagSearchFragment().apply {
                    onItemClicked = { TagMemesActivity.startWithTag(this@SearchActivity, it.tag) }
                }
                else -> throw IllegalStateException()
            }
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}
