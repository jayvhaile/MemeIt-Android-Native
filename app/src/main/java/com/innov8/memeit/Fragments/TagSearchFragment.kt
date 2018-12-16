package com.innov8.memeit.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.innov8.memeit.Adapters.ELEAdapter
import com.innov8.memeit.Adapters.TagSearchAdapter
import com.innov8.memeit.Loaders.PopularTagLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.makeLinear
import com.memeit.backend.models.Tag
import kotlinx.android.synthetic.main.fragment_search.*

class TagSearchFragment : Fragment() {
    val adapter by lazy {
        TagSearchAdapter(context!!).apply {
            onItemClicked = { this@TagSearchFragment.onItemClicked?.invoke(it) }

            onEmptyAction = { load(filterWord) }
            onErrorAction = { load(filterWord) }
            hasMore = false
        }
    }
    val loader by lazy {
        PopularTagLoader()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    fun setFilter(text: String) {
        adapter.filterWord = text
        load(if (text.isBlank()) null else text)
    }


    var onItemClicked: ((Tag) -> Unit)? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_list.apply {
            makeLinear()
            adapter = this@TagSearchFragment.adapter
        }
        load()
        swipe_refresh.setOnRefreshListener {
            load(adapter.filterWord, false)
        }

    }

    fun load(search: String? = null, showLoading: Boolean = true) {
        if (adapter.getCount() == 0) adapter.loading = showLoading
        loader.search = search?.let {
            if (it.startsWith("#"))
                it.substring(1)
            else
                it
        }
        loader.load(30, {
            adapter.loading = false
            adapter.addAll(it)
            swipe_refresh?.isRefreshing = false
        }) {
            adapter.loading = false
            swipe_refresh?.isRefreshing = false
            if (adapter.getCount() == 0) adapter.mode = ELEAdapter.MODE_ERROR
        }
    }
}