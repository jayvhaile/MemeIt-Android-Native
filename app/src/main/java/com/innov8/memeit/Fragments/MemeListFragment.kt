package com.innov8.memeit.Fragments


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memegenerator.utils.log
import com.innov8.memeit.Adapters.MemeAdapter
import com.innov8.memeit.Loaders.MemeLoader
import com.innov8.memeit.Loaders.UserMemePostsLoader
import com.innov8.memeit.Loaders.SearchMemeLoader
import com.memeit.backend.dataclasses.HomeElement
import kotlinx.android.synthetic.main.fragment_meme_list.*
import java.util.*
import com.innov8.memeit.R


class MemeListFragment : Fragment() {


    private lateinit var memeAdapter: MemeAdapter
    private lateinit var memeLoader: MemeLoader<out HomeElement>
    private var skip: Int = 0
    private var memeAdapterType: Byte = 0
    private var refresh: Boolean = false
    private var searchMemeLoader: SearchMemeLoader? = null
    private var tempList: List<HomeElement>? = null
    private var tempSkip: Int = 0
    private var searchMode: Boolean = false
    internal var disableScrollListener: Boolean = false
    private val onLoaded by lazy {
        { it: List<HomeElement> ->
            memeAdapter.loading = false
            swipe_to_refresh?.isRefreshing = false
            memeAdapter.addAll(it)
            incSkip()
        }
    }
    private val onRefreshed by lazy {
        { it: List<HomeElement> ->
            memeAdapter.loading = false
            swipe_to_refresh?.isRefreshing = false
            memeAdapter.setAll(it)
            incSkip()
        }
    }
    private val onError by lazy {
        { message: String ->
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
            }
            memeAdapter.loading = false
            swipe_to_refresh?.isRefreshing = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null)
            throw NullPointerException("Argument should not be null")
        memeAdapterType = arguments!!.getByte("adapter_type", MemeAdapter.LIST_ADAPTER)!!
        memeLoader = arguments!!.getParcelable("loader")!!
        memeAdapter = MemeAdapter.create(memeAdapterType, context!!)
    }


    fun swapAdapter(adapterType: Byte) {
        memeAdapterType = adapterType
        val elem = ArrayList(memeAdapter!!.items)
        memeAdapter = MemeAdapter.create(memeAdapterType, context!!)
        memeAdapter.addAll(elem)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipe_to_refresh.setOnRefreshListener { refresh(false) }
        meme_recycler_view.layoutManager = memeAdapter!!.createLayoutManager()
        meme_recycler_view.itemAnimator = DefaultItemAnimator()
        meme_recycler_view.adapter = memeAdapter
        if (memeAdapterType == MemeAdapter.LIST_ADAPTER || memeAdapterType == MemeAdapter.HOME_ADAPTER) {
            val itemTouchhelper = ItemTouchHelper(memeAdapter!!.make())
            itemTouchhelper.attachToRecyclerView(meme_recycler_view)
        }
        meme_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (disableScrollListener) return
                super.onScrolled(recyclerView, dx, dy)
                val llm = meme_recycler_view.layoutManager
                val visibleItemCount = llm!!.childCount
                val totalItemCount = llm.itemCount
                val fp = (llm as LinearLayoutManager).findFirstVisibleItemPosition()
                if (!memeAdapter!!.loading) {//todo jv add isLastPage checker
                    if (visibleItemCount + fp >= totalItemCount && fp >= 0) {
                        log("SCROLL LOAD")
//                        load()
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
            tempList = ArrayList(memeAdapter.items)
            memeAdapter.clear()
            tempSkip = skip
            skip = 0
        } else if (this.searchMode && !searchMode) {
            searchMemeLoader = null
            memeAdapter.setAll(ArrayList(tempList!!))
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
        memeAdapter.loading = true
        refresh = false
        if (searchMode) {
            searchMemeLoader!!.load(skip, LIMIT, onLoaded, onError)
        } else {
            memeLoader.load(skip, LIMIT, onLoaded, onError)
        }
        enableScrollListenerLater()

    }

    private fun refresh(setLoading: Boolean) {
        disableScrollListener = true
        resetSkip()
        memeAdapter!!.loading = setLoading
        refresh = true
        if (searchMode) {
            searchMemeLoader?.reset()
            searchMemeLoader?.load(skip, LIMIT, onRefreshed, onError)
        } else {
            memeLoader.reset()
            memeLoader!!.load(skip, LIMIT, onRefreshed, onError)
        }
        enableScrollListenerLater()
    }

    fun search(s: String, tags: Array<String>) {
        if (searchMode) {
            searchMemeLoader = SearchMemeLoader(s, tags)
            refresh(true)
        }
    }


    companion object {
        private val TAG = "MemeListFragment"
        private val LIMIT = 20

        fun newInstance(memeAdapterType: Byte, memeLoader: MemeLoader<out HomeElement>): MemeListFragment {
            val fragment = MemeListFragment()
            val arg = Bundle()
            arg.putByte("adapter_type", memeAdapterType)
            arg.putParcelable("loader", memeLoader)
            fragment.arguments = arg
            return fragment
        }

        fun newInstanceForUserPosts(userID: String): MemeListFragment {
            val fragment = MemeListFragment()
            val arg = Bundle()
            arg.putByte("adapter_type", MemeAdapter.GRID_ADAPTER)
            arg.putParcelable("loader", UserMemePostsLoader(userID))
            arg.putString("uid", userID)
            fragment.arguments = arg
            return fragment
        }
    }
}
