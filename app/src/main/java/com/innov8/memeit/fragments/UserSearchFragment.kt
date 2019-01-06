package com.innov8.memeit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.innov8.memeit.adapters.UserSearchAdapter
import com.innov8.memeit.loaders.SearchUserLoader
import com.innov8.memeit.R
import com.innov8.memeit.commons.ELEAdapter
import com.innov8.memeit.utils.makeLinear
import com.memeit.backend.models.User
import kotlinx.android.synthetic.main.fragment_search.*

class UserSearchFragment : Fragment() {

    val adapter by lazy {
        UserSearchAdapter(context!!).apply {
            onItemClicked = { this@UserSearchFragment.onItemClicked?.invoke(it) }
            onEmptyAction = { load(this.filterWord) }
            onErrorAction = { load(this.filterWord) }
            hasMore = false
        }
    }
    val loader by lazy {
        SearchUserLoader()
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

    var onItemClicked: ((User) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_list.apply {
            makeLinear()
            adapter = this@UserSearchFragment.adapter
        }

        swipe_refresh.setOnRefreshListener {
            load(adapter.filterWord, false)
        }
        load()

    }

    fun load(search: String? = null, showLoading: Boolean = true) {
        if (adapter.getCount() == 0) adapter.loading = showLoading
        loader.apply {
            search?.let {
                if (it.startsWith("@")) {
                    name = null
                    username = it.substring(1)
                } else {
                    name = it
                    username = it
                }
            }
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