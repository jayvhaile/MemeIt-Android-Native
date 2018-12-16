package com.innov8.memeit.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.Loaders.MemeLoader
import com.innov8.memeit.Loaders.UserMemePostsLoader
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.R
import com.innov8.memeit.Utils.snack
import com.memeit.backend.models.HomeElement
import kotlinx.android.synthetic.main.fragment_meme_list.*


class MemeListFragment : Fragment() {

    private val memeAdapter by lazy {

        MemeAdapter.create(arguments!!.getByte("adapter_type", -1), context!!)
    }
    val memeLoader by lazy {
        arguments!!.getParcelable<MemeLoader<out HomeElement>>("loader")!!
    }

    val loaderAdapterHandler by lazy {
        LoaderAdapterHandler(memeAdapter, memeLoader).apply {
            onLoaded = { swipe_to_refresh?.isRefreshing = false }
            onLoadFailed = {
                view?.snack(it)
                swipe_to_refresh?.isRefreshing = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null)
            throw NullPointerException("Argument should not be null")
        this.retainInstance = true
        loaderAdapterHandler.load()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        swipe_to_refresh.setOnRefreshListener { loaderAdapterHandler.refresh(false) }
        meme_recycler_view.layoutManager = memeAdapter.createLayoutManager()
        meme_recycler_view.itemAnimator = DefaultItemAnimator()
        meme_recycler_view.adapter = memeAdapter

    }





    companion object {

        fun newInstance(memeAdapterType: Byte, memeLoader: MemeLoader<out HomeElement>): MemeListFragment {
            val fragment = MemeListFragment()
            val arg = Bundle()
            arg.putByte("adapter_type", memeAdapterType)
            arg.putParcelable("loader", memeLoader)
            fragment.arguments = arg
            return fragment
        }

        fun newInstanceForUserPosts(userID: String, memeAdapterType: Byte = MemeAdapter.GRID_ADAPTER_MY_POSTS): MemeListFragment {
            val fragment = MemeListFragment()
            val arg = Bundle()
            arg.putByte("adapter_type", memeAdapterType)
            arg.putParcelable("loader", UserMemePostsLoader(userID))
            arg.putString("uid", userID)
            fragment.arguments = arg
            return fragment
        }
    }
}
