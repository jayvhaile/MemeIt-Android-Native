package com.innov8.memeit.activities

import android.os.Bundle
import com.innov8.memeit.commons.SuperActivity
import com.innov8.memeit.adapters.ReactorAdapter
import com.innov8.memeit.R
import com.innov8.memeit.loaders.ReactorLoader
import com.innov8.memeit.commons.LoaderAdapterHandler
import com.innov8.memeit.utils.makeLinear
import kotlinx.android.synthetic.main.activity_reactor_list.*
import java.lang.IllegalStateException

class ReactorListActivity : SuperActivity() {

    private val adapter by lazy {
        ReactorAdapter(this)
    }
    private val loader by lazy {
        ReactorLoader(intent?.getStringExtra("mid")
                ?: throw IllegalStateException("Should pass mid(Reactions)"))
    }
    private val handler by lazy {
        LoaderAdapterHandler(adapter, loader).apply {
            onLoaded = { reactor_swipe_refresh.isRefreshing = false }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reactor_list)
        reactors_list.makeLinear()
        reactors_list.adapter = adapter

        reactor_swipe_refresh.setOnRefreshListener {
            handler.refresh()
        }
        handler.load()
    }
}
