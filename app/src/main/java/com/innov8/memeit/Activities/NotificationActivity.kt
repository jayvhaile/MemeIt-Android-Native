package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.Adapters.NotificationAdapter
import com.innov8.memeit.Loaders.MyNotificationLoader
import com.innov8.memeit.Loaders.TestNotificationLoader
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.R
import com.innov8.memeit.Utils.makeLinear
import com.memeit.backend.models.*
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    private val notificationAdapter by lazy {
        NotificationAdapter(this)
    }
    private val loaderAdapter by lazy {
        LoaderAdapterHandler(notificationAdapter, MyNotificationLoader()).apply {
            onLoaded = {
                swipe_refresh?.isRefreshing = false
                MemeItUsers.markNotificationSeen().call {}
            }
            onLoadFailed = { message ->
                swipe_refresh?.isRefreshing = false
                swipe_refresh?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        notif_list.apply {
            makeLinear()
            adapter = notificationAdapter
        }
        swipe_refresh.setOnRefreshListener {
            loaderAdapter.refresh()
        }
        setSupportActionBar(notf_toolbar)
        supportActionBar?.apply {
            title = "Notifications"
            setDisplayHomeAsUpEnabled(true)
        }
        loaderAdapter.load()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
