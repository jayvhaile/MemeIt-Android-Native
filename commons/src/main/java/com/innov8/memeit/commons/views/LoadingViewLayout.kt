package com.innov8.memeit.commons.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.innov8.memeit.commons.R

class LoadingViewLayout : FrameLayout {
    companion object {
        const val STATUS_LOADING = 0
        const val STATUS_LOADED = 1
        const val STATUS_ERROR = 2
    }

    var status = STATUS_LOADING
        private set

    fun changeStatus(status: Int) {
        when (status) {
            STATUS_LOADING -> setLoading()
            STATUS_ERROR -> setError()
            STATUS_LOADED -> setLoaded()
            else -> throw IllegalArgumentException()
        }
    }

    var contentView: View? = null
        set(value) {
            field = value
            addView(field, lp)
            changeStatus(status)
        }

    fun setContentView(id: Int) {
        contentView = LayoutInflater.from(context).inflate(id, this, false)
    }

    private val loadingView: View = LayoutInflater.from(context).inflate(R.layout.loading_view, this, false)
    var onRetry: (() -> Unit)? = null
    private val loaderView: View = loadingView.findViewById(R.id.loading_view_loader)
    private val retryView: View = loadingView.findViewById(R.id.loading_view_refresh)
    private val errorView: TextView = loadingView.findViewById(R.id.loading_view_error)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    fun init() {
        addView(loadingView, lp)
        retryView.setOnClickListener {
            setLoading()
            onRetry?.invoke()
        }
        setLoading()
    }

    fun setLoading() {
        loadingView.visibility = View.VISIBLE
        contentView?.visibility = View.GONE
        retryView.visibility = View.GONE
        errorView.visibility = View.GONE
        loaderView.visibility = View.VISIBLE
        status = STATUS_LOADING
    }

    fun setError(error: String = "Failed to load") {
        loadingView.visibility = View.VISIBLE
        contentView?.visibility = View.GONE
        retryView.visibility = View.VISIBLE
        errorView.visibility = View.VISIBLE
        loaderView.visibility = View.GONE
        errorView.text = error
        status = STATUS_ERROR
    }

    fun setLoaded() {
        loadingView.visibility = View.GONE
        contentView?.visibility = View.VISIBLE
        status = STATUS_LOADED
    }

    fun setErrorText(error: String, setErrorMode: Boolean = false) {
        if (setErrorMode)
            setError(error)
        else
            errorView.text = error
    }

}