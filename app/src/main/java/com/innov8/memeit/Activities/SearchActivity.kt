package com.innov8.memeit.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.innov8.memeit.Fragments.MemeListFragment
import com.innov8.memeit.R
import com.innov8.memeit.Utils.addOnTabSelected
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        search_tabs.setupWithViewPager(search_pager)

        search_view.setOnEditorActionListener { v, actionId, event ->
            when (search_tabs.selectedTabPosition) {
                0 -> searchMemes(search_view.text.toString())
                1 -> searchMemes(search_view.text.toString())
                2 -> searchMemes(search_view.text.toString())
            }
            true
        }
        search_tabs.addOnTabSelected {
            when (it.position) {
                0 -> {
                    search_view.hint = "Search Memes, Use #tag to search by tag"
                }
                1 -> {
                    search_view.hint = "Search People"
                }
                2 -> {
                    search_view.hint = "Search Tags"
                }
            }
        }
    }

    private fun searchMemes(search: String) {

    }

    private fun searchUsers(search: String) {

    }

    private fun searchTags(search: String) {

    }


    companion object {

    }

    class SearchPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val titles = listOf("Memes", "People", "Tags")

        override fun getItem(position: Int): Fragment {
            return when (position) {
                else -> MemeListFragment()

            }
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}
