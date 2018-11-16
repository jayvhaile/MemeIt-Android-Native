package com.innov8.memeit.Activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.Adapters.UserListAdapter
import com.innov8.memeit.Loaders.FollowerLoader
import com.innov8.memeit.Loaders.FollowingLoader
import com.innov8.memeit.Loaders.UserListLoader
import com.innov8.memeit.MLHandler
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.dataclasses.User
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    private lateinit var followerAdapter: UserListAdapter

    private lateinit var userListLoader: UserListLoader

    private lateinit var ml: MLHandler<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)


        userListLoader = intent?.getParcelableExtra("loader") ?: throw NullPointerException("Argument should not be null")
        val myUID = MemeItClient.myUser!!.id
        val ull = userListLoader
        val desc = when (ull) {
            is FollowerLoader -> {
                if (ull.uid == myUID) {
                    "You have no followers"
                } else "User have no followers"
            }
            is FollowingLoader -> {
                if (ull.uid == myUID) {
                    "You are not following anyone"
                } else "User not following anyone"
            }
            else -> ""
        }

        followerAdapter = UserListAdapter(this, desc)
        ml = MLHandler(followerAdapter, userListLoader)
        ml.onLoaded = { swipe_to_refresh?.isRefreshing = false }
        ml.onLoadFailed = { message ->
            swipe_to_refresh?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
            swipe_to_refresh?.isRefreshing = false
        }
        if (savedInstanceState != null) {
            val users: Array<User> = savedInstanceState.getParcelableArray("users") as Array<User>
            followerAdapter.setAll(users.toList())
        } else
            ml.load()

        initView()
    }

    private fun initView() {
        swipe_to_refresh.setOnRefreshListener { ml.refresh() }
        followers_recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val animator = DefaultItemAnimator()
        followers_recycler_view.itemAnimator = animator
        followers_recycler_view.adapter = followerAdapter

        setSupportActionBar(toolbar_user_list)

        supportActionBar?.title = if (userListLoader is FollowingLoader) "Followings" else "Followers"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArray("users", followerAdapter.items.toTypedArray())
        super.onSaveInstanceState(outState)
    }

    companion object {
        fun startActivity(context: Context, userLoader: UserListLoader) {
            context.startActivity(Intent(context, UserListActivity::class.java).apply {
                putExtra("loader", userLoader)
            })
        }

    }
}
