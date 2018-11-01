package com.innov8.memeit.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Loaders.MemeLoader
import com.innov8.memeit.Loaders.SearchMemeLoader
import com.innov8.memeit.Loaders.UserMemePostsLoader
import com.innov8.memeit.MLHandler
import com.innov8.memeit.R
import com.memeit.backend.dataclasses.HomeElement
import kotlinx.android.synthetic.main.fragment_meme_list.*
import java.util.*


class MemeListFragment : Fragment() {


    private lateinit var memeAdapter: MemeAdapter
    private lateinit var memeLoader: MemeLoader<out HomeElement>
    private var memeAdapterType: Byte = 0
    private lateinit var searchMemeLoader: SearchMemeLoader
    private var tempList: List<HomeElement>? = null
    private var searchMode: Boolean = false

    lateinit var ml: MLHandler<HomeElement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null)
            throw NullPointerException("Argument should not be null")
        memeAdapterType = arguments!!.getByte("adapter_type", MemeAdapter.LIST_ADAPTER)!!
        memeLoader = arguments!!.getParcelable("loader")!!
        searchMemeLoader = SearchMemeLoader()
        memeAdapter = MemeAdapter.create(memeAdapterType, context!!)
        ml = MLHandler(memeAdapter, memeLoader)
        ml.onLoaded = { swipe_to_refresh?.isRefreshing = false }
        ml.onLoadFailed = { message ->
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
            }
            swipe_to_refresh?.isRefreshing = false
        }

        ml.load()
    }


    fun swapAdapter(adapterType: Byte) {
        memeAdapterType = adapterType
        val elem = ArrayList(memeAdapter.items)
        memeAdapter = MemeAdapter.create(memeAdapterType, context!!)
        memeAdapter.addAll(elem)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        swipe_to_refresh.setOnRefreshListener { ml.refresh(false) }
        meme_recycler_view.layoutManager = memeAdapter.createLayoutManager()
        meme_recycler_view.itemAnimator = DefaultItemAnimator()
        meme_recycler_view.adapter = memeAdapter
        if (memeAdapterType == MemeAdapter.LIST_ADAPTER || memeAdapterType == MemeAdapter.HOME_ADAPTER) {
            val itemTouchhelper = ItemTouchHelper(memeAdapter.make())
            itemTouchhelper.attachToRecyclerView(meme_recycler_view)
        }
    }

    fun setSearchMode(searchMode: Boolean) {
        if (!this.searchMode && searchMode) {
            tempList = ArrayList(memeAdapter.items)
            memeAdapter.clear()
            ml.loader = searchMemeLoader
        } else if (this.searchMode && !searchMode) {
            memeAdapter.setAll(ArrayList(tempList ?: arrayListOf()))
            tempList = null
            ml.loader = memeLoader
        }
        this.searchMode = searchMode
    }


    fun search(s: String, tags: Array<String>) {
        if (searchMode) {
            searchMemeLoader.search = s
            searchMemeLoader.tags = tags
            ml.refresh(true)
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
