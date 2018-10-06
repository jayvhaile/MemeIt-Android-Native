package com.innov8.memeit.Fragments


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.innov8.memegenerator.utils.*
import com.innov8.memeit.Adapters.MemeAdapter
import com.innov8.memeit.CustomClasses.EmptyLoader
import com.innov8.memeit.CustomClasses.MemeLoader
import com.innov8.memeit.CustomClasses.SearchLoader
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.HomeElement
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.utilis.OnCompleteListener

import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.innov8.memeit.CustomClasses.MyMemesLoader

class MemeListFragment : Fragment() {

    private var memeList: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var memeAdapter: MemeAdapter? = null

    private val emptyLoader = EmptyLoader()
    private var memeLoader: MemeLoader<*>? = null


    private var skip: Int = 0

    private var memeAdapterType: Byte = 0
    private var memeLoaderType: Byte = 0


    private var refresh: Boolean = false


    internal var searchLoader: SearchLoader? = null
    internal var tempList: List<HomeElement>? = null
    internal var tempSkip: Int = 0

    internal var searchMode: Boolean = false

    internal var searchText: String? = null
    internal var searchTags: Array<String>? = null
    internal var disableScrollListener: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null)
            throw NullPointerException("Argument should not be null")
        memeAdapterType = arguments!!.getByte("adapter_type", MemeAdapter.LIST_ADAPTER)!!
        memeLoaderType = arguments!!.getByte("loader_type", MemeLoader.EMPTY_LOADER)!!
        val onLoaded:(List<Meme>)->Unit={
            memeAdapter!!.loading = false
            swipeRefreshLayout!!.isRefreshing = false
            memeAdapter!!.addAll(it)
            incSkip()
        }
        val onRefreshed:(List<Meme>)->Unit={
            memeAdapter!!.loading = false
            swipeRefreshLayout!!.isRefreshing = false
            memeAdapter!!.setAll(it)
            incSkip()
        }
        val onError:(String)->Unit={
            context?.toast(it)
            memeAdapter?.loading=false
            swipeRefreshLayout!!.isRefreshing = false

        }
        memeAdapter = MemeAdapter.create(memeAdapterType, context!!)
        initLoader()
    }

    private fun initLoader() {
        if (searchMode && searchLoader == null) {
            searchLoader = SearchLoader()
        }
        if (memeLoader == null)
            if (memeLoaderType == MemeLoader.USER_POST_MEME_LOADER) {
                val uid = arguments!!.getString("uid")
                memeLoader = MemeLoader.create(memeLoaderType, context!!, uid)
            } else {
                memeLoader = MemeLoader.create(memeLoaderType, context!!, null)
            }
    }


    private fun initAdapter() {
        if (memeAdapter == null)
            memeAdapter = MemeAdapter.create(memeAdapterType, context!!)
    }

    fun swapAdapter(adapterType: Byte) {
        memeAdapterType = adapterType
        val elem = ArrayList(memeAdapter!!.items)
        memeAdapter = null
        initAdapter()
        memeAdapter!!.addAll(elem)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh)
        memeList = view.findViewById(R.id.meme_recycler_view)
        swipeRefreshLayout!!.setOnRefreshListener { refresh(false) }
        memeList!!.layoutManager = memeAdapter!!.createLayoutManager()
        val animator = DefaultItemAnimator()
        memeList!!.itemAnimator = animator
        memeList!!.adapter = memeAdapter
        if (memeAdapterType == MemeAdapter.LIST_ADAPTER || memeAdapterType == MemeAdapter.HOME_ADAPTER) {
            val itemTouchhelper = ItemTouchHelper(memeAdapter!!.make())
            itemTouchhelper.attachToRecyclerView(memeList)
        }

        memeList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (disableScrollListener) return
                super.onScrolled(recyclerView, dx, dy)
                val llm = memeList!!.layoutManager
                val visibleItemCount = llm!!.childCount
                val totalItemCount = llm.itemCount
                val fp = (llm as LinearLayoutManager).findFirstVisibleItemPosition()
                if (!memeAdapter!!.loading) {//todo jv add isLastPage checker
                    if (visibleItemCount + fp >= totalItemCount && fp >= 0) {
                        log("SCROLL LOAD")
                        load()
                    }
                }
                //todo load more at the end
            }
        })
        load()
    }


    private fun resetSkip() {
        skip = 0
    }

    private fun incSkip() {
        skip += LIMIT
    }

    fun setSearchMode(searchMode: Boolean) {
        disableScrollListener = true
        if (!this.searchMode && searchMode) {
            searchLoader = SearchLoader()
            tempList = ArrayList(memeAdapter!!.items)
            memeAdapter!!.clear()
            log("searchmode true")
            tempSkip = skip
            skip = 0
        } else if (this.searchMode && !searchMode) {
            searchLoader = null
            searchText = null
            searchTags = null
            log("searchmode false")
            memeAdapter!!.setAll(ArrayList(tempList!!))
            skip = tempSkip
            tempSkip = 0
            tempList = null
        }
        enableScrollListenerLater()
        this.searchMode = searchMode
    }

    private fun enableScrollListenerLater() {
        Handler().postDelayed({ disableScrollListener = false }, 100)
    }

    private fun load() {
        disableScrollListener = true
        memeAdapter!!.loading = true
        refresh = false
        if (searchMode) {
            searchLoader!!.search(searchText, searchTags, skip, LIMIT)
        } else {
            memeLoader!!.load(skip, LIMIT)
        }
        enableScrollListenerLater()


    }

    private fun refresh(setLoading: Boolean) {
        disableScrollListener = true
        resetSkip()
        memeAdapter!!.loading = setLoading
        refresh = true
        if (searchMode) {
            searchLoader!!.reset()
            searchLoader!!.listener = refreshListener
            searchLoader!!.search(searchText, searchTags, skip, LIMIT)
        } else {
            memeLoader!!.reset()
            memeLoader!!.listener = refreshListener
            memeLoader!!.load(skip, LIMIT)
        }
        enableScrollListenerLater()
    }

    fun search(s: String, tags: Array<String>) {
        if (searchMode) {
            searchText = s
            searchTags = tags
            refresh(true)
        }
    }


    companion object {
        private val TAG = "MemeListFragment"
        private val LIMIT = 20

        fun newInstance(memeAdapterType: Byte, memeLoaderType: Byte): MemeListFragment {
            val fragment = MemeListFragment()
            val arg = Bundle()
            arg.putByte("adapter_type", memeAdapterType)
            arg.putByte("loader_type", memeLoaderType)
            fragment.arguments = arg
            return fragment
        }

        fun newInstanceForUserPosts(userID: String): MemeListFragment {
            val fragment = MemeListFragment()
            val arg = Bundle()
            arg.putByte("adapter_type", MemeAdapter.GRID_ADAPTER)
            arg.putParcelable("loader_type", MyMemesLoader(userID))
            arg.putString("uid", userID)
            fragment.arguments = arg
            return fragment
        }
    }
}// Required empty public constructor
