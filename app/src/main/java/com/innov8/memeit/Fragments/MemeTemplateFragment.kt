package com.innov8.memeit.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.innov8.memeit.Adapters.TemplateAdapter
import com.innov8.memeit.Loaders.Sorter
import com.innov8.memeit.Loaders.TemplateLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.FilterableLoaderAdapterHandler
import com.innov8.memeit.Utils.makeLinear
import com.innov8.memeit.commons.toast
import com.memeit.backend.models.MemeTemplate
import kotlinx.android.synthetic.main.fragment_meme_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File


class MemeTemplateFragment : Fragment() {


    private val templateLoader by lazy {
        arguments!!.getParcelable<TemplateLoader>("loader")!!
    }
    private val adapter by lazy {
        when (templateLoader.sortBy) {
            Sorter.POPULAR -> TemplateAdapter(context!!)
            Sorter.RECENT -> TemplateAdapter(context!!, Comparator { o1, o2 ->
                o2.createdDate.compareTo(o1.createdDate)
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
        val vm = ViewModelProviders.of(activity!!).get(MemeTemplateViewModel::class.java)
        vm.workinfos.observe(this, Observer {
            adapter.workInfos = it
        })
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            adapter.saved = withContext(Dispatchers.Default) {
                File(context!!.filesDir, "templates/json")
                        .takeIf { it.exists() }
                        ?.list()
                        ?: arrayOf()
            }
        }
        handler.load()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meme_recycler_view.makeLinear(RecyclerView.VERTICAL)
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