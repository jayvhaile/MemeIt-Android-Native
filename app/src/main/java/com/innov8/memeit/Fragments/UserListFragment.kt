package com.innov8.memeit.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.Adapters.UserListAdapter
import com.innov8.memeit.Loaders.FollowerLoader
import com.innov8.memeit.Loaders.UserListLoader
import com.innov8.memeit.MLHandler
import com.memeit.backend.dataclasses.User
import com.innov8.memeit.R

import kotlinx.android.synthetic.main.fragment_followers.*

/**
 * A simple [Fragment] subclass.
 */
class UserListFragment : Fragment() {

    private lateinit var followerAdapter: UserListAdapter

    private lateinit var userListLoader: UserListLoader

    private lateinit var ml: MLHandler<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userListLoader = arguments?.getParcelable("loader") ?: throw NullPointerException("Argument should not be null")
        val desc=if(userListLoader is FollowerLoader) "You have no followers" else "You are not following anyone"
        followerAdapter = UserListAdapter(context!!,desc)
        ml = MLHandler(followerAdapter, userListLoader)
        ml.onLoaded = { swipe_to_refresh?.isRefreshing = false }
        ml.onLoadFailed = { message ->
            view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()}
            swipe_to_refresh?.isRefreshing = false
        }
        if (savedInstanceState != null) {
            val users: Array<User> = savedInstanceState.getParcelableArray("users") as Array<User>
            followerAdapter.setAll(users.toList())
        } else
            ml.load()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_to_refresh.setOnRefreshListener { ml.refresh() }
        followers_recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val animator = DefaultItemAnimator()
        followers_recycler_view.itemAnimator = animator
        followers_recycler_view.adapter = followerAdapter

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArray("users", followerAdapter.items.toTypedArray())
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val LIMIT = 50

        fun newInstance(userLoader: UserListLoader): UserListFragment {
            val fragment = UserListFragment()
            val arg = Bundle()
            arg.putParcelable("loader", userLoader)
            fragment.arguments = arg
            return fragment
        }

    }
}
