package com.innov8.memeit.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innov8.memeit.loaders.UnapprovedTemplateLoader
import com.innov8.memeit.R
import com.innov8.memeit.commons.LoaderAdapterHandler
import com.innov8.memeit.utils.makeLinear
import com.innov8.memeit.admin.Adapters.TemplateManagerAdapter
import com.innov8.memeit.commons.toast
import kotlinx.android.synthetic.admin.activity_template_manager.*

class TemplateManagerActivity : AppCompatActivity() {
    private val loader by lazy {
        UnapprovedTemplateLoader()
    }
    private val adapter by lazy {
        TemplateManagerAdapter(this)
    }
    private val handler by lazy {
        LoaderAdapterHandler(adapter, loader).apply {
            onLoaded = { swipe_to_refresh?.isRefreshing = false }
            onLoadFailed = {
                toast(it)
                swipe_to_refresh?.isRefreshing = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_manager)
        template_list.apply {
            makeLinear()
            adapter = this@TemplateManagerActivity.adapter
        }
        handler.load()
        swipe_to_refresh.setOnRefreshListener {
            handler.refresh()
        }
    }
}
