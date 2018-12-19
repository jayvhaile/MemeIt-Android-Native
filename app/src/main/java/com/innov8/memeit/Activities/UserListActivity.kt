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
import com.innov8.memeit.Fragments.UserListFragment
import com.innov8.memeit.Loaders.FollowerLoader
import com.innov8.memeit.Loaders.FollowingLoader
import com.innov8.memeit.Loaders.UserListLoader
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItClient.context
import com.memeit.backend.models.User
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        supportFragmentManager.beginTransaction()
                .add(R.id.holder, UserListFragment.newInstance(intent.getParcelableExtra("loader")))
                .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun startActivity(context: Context, userLoader: UserListLoader) {
            context.startActivity(Intent(context, UserListActivity::class.java).apply {
                putExtra("loader", userLoader)
            })
        }

    }
}
