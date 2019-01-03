package com.innov8.memeit.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.adroitandroid.chipcloud.ChipCloud
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memeit.utils.MyFragmentPagerAdapter
import com.innov8.memeit.loaders.SavedTemplatesLoader
import com.innov8.memeit.loaders.ServerTemplateLoader
import com.innov8.memeit.loaders.Sorter
import com.innov8.memeit.R
import com.innov8.memeit.customViews.SearchToolbar
import com.innov8.memeit.utils.addOnTabSelected
import com.innov8.memeit.utils.onChipSelected
import com.memeit.backend.models.Meme
import kotlinx.android.synthetic.main.fragment_meme_template.*

class MemeTemplateHolderFragment : Fragment() {
    private val pager by lazy {
        TemplatePager(childFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_template, container, false)
    }

    private val currentFrag: MemeTemplateFragment
        get() = pager.getFragmentAt(template_pager.currentItem) as MemeTemplateFragment

    private val filterView by lazy {
        layoutInflater.inflate(R.layout.dialog_template_filter, null, false)
    }

    private val types = arrayOf(null, Meme.MemeType.IMAGE.name, Meme.MemeType.GIF.name)
    private val categories by lazy {
        mutableListOf<String?>(null).apply {
            addAll(resources.getStringArray(R.array.template_categories))
        }
    }

    private var typeIndex = 0
    private var categoryIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        template_pager.adapter = pager
        template_tabs.setupWithViewPager(template_pager)
        search_toolbar.inflateMenu(R.menu.template_menu)
        val sm = search_toolbar.menu.findItem(R.id.menu_template_search)
        sm.actionView = SearchToolbar(context!!).apply {
            sm.setOnActionExpandListener(this)
            onSearch = {
                currentFrag.setSearch(it, true, false)
            }
            onSearchDone = {
                currentFrag.setSearch(it)
            }
        }

        search_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_template_search -> {

                }
                R.id.menu_template_category -> {

                    val memeType: ChipCloud = filterView.findViewById(R.id.type_chips)
                    val categoryType: ChipCloud = filterView.findViewById(R.id.category_chips)

                    memeType.apply {
                        setSelectedChip(typeIndex)
                        onChipSelected { typeIndex = it }
                    }

                    categoryType.apply {
                        setSelectedChip(categoryIndex)
                        onChipSelected { categoryIndex = it }
                    }
                    MaterialDialog.Builder(context!!)
                            .title("Filter Templates")
                            .customView(filterView, false)
                            .positiveText("Apply")
                            .negativeText("Cancel")
                            .onPositive { _, _ ->
                                applyFilter()
                            }.show()


                }
            }
            true
        }
        template_tabs.addOnTabSelected {
            applyFilter()
        }

    }

    private fun applyFilter() {
        currentFrag.apply {
            setType(types[typeIndex], false)
            setCategory(categories[categoryIndex], false)
            filter()
        }
    }

    inner class TemplatePager(fragmentManager: FragmentManager) : MyFragmentPagerAdapter(fragmentManager) {
        private val titles = arrayOf("Recent", "Popular", "Saved", "My Templates")


        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MemeTemplateFragment.newInstance(ServerTemplateLoader().apply {
                    sortBy = Sorter.RECENT
                })
                1 -> MemeTemplateFragment.newInstance(ServerTemplateLoader())
                2 -> MemeTemplateFragment.newInstance(SavedTemplatesLoader())
                3 -> MemeTemplateFragment.newInstance(ServerTemplateLoader().apply {
                    sortBy = Sorter.RECENT
                    mine = true
                })
                else -> throw IllegalStateException()
            }
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]

    }
}