package com.innov8.memeit.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innov8.memegenerator.utils.toast
import com.innov8.memeit.Adapters.UserListAdapter
import com.innov8.memeit.Loaders.UserListLoader
import com.memeit.backend.dataclasses.User
import com.innov8.memeit.R

import kotlinx.android.synthetic.main.fragment_followers.*

/**
 * A simple [Fragment] subclass.
 */
class UserListFragment : Fragment() {

    private lateinit var followerAdapter: UserListAdapter

    private var skip: Int = 0
    private lateinit var userListLoader: UserListLoader

    private val onLoaded by lazy {
        {it:List<User>->
            swipe_to_refresh!!.isRefreshing = false
            followerAdapter.addAll(it)
            incSkip()
        }
    }
    private val onRefreshed by lazy {
        {it:List<User>->
            swipe_to_refresh!!.isRefreshing = false
            followerAdapter.setAll(it)
            incSkip()
        }
    }
    private val onError by lazy {
        {it:String->
            context?.toast(it)
            swipe_to_refresh.isRefreshing = false

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null)
            throw NullPointerException("Argument should not be null")

        userListLoader = arguments!!.getParcelable("loader")!!


        followerAdapter = UserListAdapter(context!!)
        if (savedInstanceState != null) {
            val users = savedInstanceState.getParcelableArrayList<User>("users")
            followerAdapter!!.setAll(users!!)
        } else
            load()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_to_refresh.setOnRefreshListener { refresh() }
        followers_recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val animator = DefaultItemAnimator()
        followers_recycler_view.itemAnimator = animator
        followers_recycler_view.adapter = followerAdapter

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("users", followerAdapter!!.items)
        super.onSaveInstanceState(outState)
    }

    private fun load() {
        userListLoader.load(skip, LIMIT,onLoaded,onError)
    }

    private fun refresh() {
        resetSkip()
        userListLoader.load(skip, LIMIT,onLoaded,onError)
    }

    private fun resetSkip() {
        skip = 0
    }

    private fun incSkip() {
        skip += LIMIT
    }


    companion object {
        private val LIMIT = 50

        fun newInstance(userLoader: UserListLoader): UserListFragment {
            val fragment = UserListFragment()
            val arg = Bundle()
            arg.putParcelable("loader", userLoader)
            fragment.arguments = arg
            return fragment
        }

    }

}// Required empty public constructor
