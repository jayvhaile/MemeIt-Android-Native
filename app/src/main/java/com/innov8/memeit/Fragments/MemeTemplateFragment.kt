package com.innov8.memeit.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.utils.initWithGrid
import com.innov8.memeit.Adapters.ELEAdapter
import com.innov8.memeit.Adapters.MemeAdapters.GridMemeAdapter
import com.innov8.memeit.Adapters.TemplateAdapter
import com.innov8.memeit.Loaders.Sorter
import com.innov8.memeit.Loaders.TemplateLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.FilterableLoaderAdapterHandler
import com.innov8.memeit.commons.toast
import kotlinx.android.synthetic.main.fragment_meme_list.*


class MemeTemplateFragment : Fragment() {


    private val templateLoader by lazy {
        arguments!!.getParcelable<TemplateLoader>("loader")!!
    }
    private val adapter by lazy {
        when (templateLoader.sortBy) {
            Sorter.POPULAR -> TemplateAdapter(context!!)
            Sorter.RECENT -> TemplateAdapter(context!!, Comparator { o1, o2 ->
                (o2.createdDate ?: 0).compareTo(o1.createdDate ?: 0)
            })
        }
    }
    private val handler by lazy {
        FilterableLoaderAdapterHandler(adapter, templateLoader).apply {
            onLoaded = { swipe_to_refresh?.isRefreshing = false }
            onLoadFailed = {
                context?.toast(it)
                swipe_to_refresh?.isRefreshing = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler.load()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        meme_recycler_view.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        ELEAdapter.TYPE_EMPTY, ELEAdapter.TYPE_ERROR,
                        ELEAdapter.TYPE_LOADING, ELEAdapter.TYPE_LOAD_MORE -> 2
                        else -> 1
                    }
                }
            }
        }
        meme_recycler_view.adapter = adapter
        swipe_to_refresh.setOnRefreshListener {
            handler.refresh(false)
        }
    }

    fun search(search: String) {
        adapter.filterWord = search
        handler.refresh()
    }

    companion object {
        fun newInstance(templateLoader: TemplateLoader): MemeTemplateFragment {
            return MemeTemplateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("loader", templateLoader)
                }
            }
        }
    }
}