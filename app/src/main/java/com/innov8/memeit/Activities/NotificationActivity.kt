package com.innov8.memeit.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.innov8.memeit.Adapters.NotificationAdapter
import com.innov8.memeit.Loaders.MyNotificationLoader
import com.innov8.memeit.Utils.LoaderAdapterHandler
import com.innov8.memeit.R
import com.memeit.backend.models.*
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    lateinit var notificationAdapter: NotificationAdapter
    lateinit var loaderAdapter: LoaderAdapterHandler<Notification>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationAdapter = NotificationAdapter(this)
        notif_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        notif_list.adapter = notificationAdapter
        swipe_refresh.setOnRefreshListener {
            loaderAdapter.refresh()
        }
        setSupportActionBar(notf_toolbar)
        supportActionBar?.title = "Notifications"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loaderAdapter = LoaderAdapterHandler(notificationAdapter, MyNotificationLoader())
        loaderAdapter.onLoaded = {
            swipe_refresh?.isRefreshing = false
            MemeItUsers.markNotificationSeen().call {}
        }
        loaderAdapter.onLoadFailed = { message ->
            swipe_refresh?.isRefreshing = false
            swipe_refresh?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
        }
        loaderAdapter.load()
    }
}
