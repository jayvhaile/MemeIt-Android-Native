package com.innov8.memeit.Fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.innov8.memegenerator.utils.initWithGrid
import com.innov8.memeit.Adapters.DraftsAdapter
import com.innov8.memeit.Loaders.DraftLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.commons.toast
import kotlinx.android.synthetic.main.fragment_meme_list.*

class DraftsFragment : Fragment() {

    private val loader by lazy {
        DraftLoader()
    }
    private val adapter by lazy {
        DraftsAdapter(context!!)
    }
    private val handler by lazy {
        LoaderAdapterHandler(adapter, loader).apply {
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

        meme_recycler_view.apply {
            initWithGrid(2)
            adapter = this@DraftsFragment.adapter
        }


    }
}