package com.innov8.memeit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.adapters.UserListAdapter
import com.innov8.memeit.loaders.FollowerLoader
import com.innov8.memeit.loaders.FollowingLoader
import com.innov8.memeit.loaders.UserListLoader
import com.innov8.memeit.R
import com.innov8.memeit.commons.LoaderAdapterHandler
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.User
import kotlinx.android.synthetic.main.fragment_user_list.*

class UserListFragment : Fragment() {

    companion object {
        const val PARAM_LOADER = "loader"
        const val PARAM_SHOW_FOLLOW = "show follow"
        fun newInstance(userListLoader: UserListLoader): UserListFragment {
            return UserListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PARAM_LOADER, userListLoader)
                }
            }
        }
    }


    private val userListLoader by lazy {
        arguments?.getParcelable<UserListLoader>(PARAM_LOADER)!!
    }


    private val userListAdapter by lazy {
        val myUID = MemeItClient.myUser!!.id
        val desc = when (userListLoader) {
            is FollowerLoader -> {
                if ((userListLoader as FollowerLoader).uid == myUID) {
                    "You have no followers"
                } else "User have no followers"
            }
            is FollowingLoader -> {
                if ((userListLoader as FollowingLoader).uid == myUID) {
                    "You are not following anyone"
                } else "User not following anyone"
            }
            else -> ""
        }
        UserListAdapter(this.context!!, desc)
    }
    private val loaderAdapter by lazy {
        LoaderAdapterHandler(userListAdapter, userListLoader).apply {
            onLoaded = { swipe_to_refresh?.isRefreshing = false }
            onLoadFailed = { message ->
                swipe_to_refresh?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
                swipe_to_refresh?.isRefreshing = false
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderAdapter.load()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_to_refresh.setOnRefreshListener { loaderAdapter.refresh() }
        followers_recycler_view.layoutManager = LinearLayoutManager(MemeItClient.context, RecyclerView.VERTICAL, false)
        val animator = DefaultItemAnimator()
        followers_recycler_view.itemAnimator = animator
        followers_recycler_view.adapter = userListAdapter

        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar_user_list)
            supportActionBar?.title = if (userListLoader is FollowingLoader) "Followings" else "Followers"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

    }

}