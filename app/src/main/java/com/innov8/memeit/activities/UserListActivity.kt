package com.innov8.memeit.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.fragments.UserListFragment
import com.innov8.memeit.loaders.UserListLoader
import com.innov8.memeit.R

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
