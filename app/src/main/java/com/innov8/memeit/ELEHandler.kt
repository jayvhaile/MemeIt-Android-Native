package com.innov8.memeit

import com.innov8.memeit.Adapters.ELEAdapter
import com.innov8.memeit.Adapters.ELEListAdapter
import com.innov8.memeit.Loaders.Loader

abstract class LoadingHandler<A : ELEAdapter<*>>(val adapter: A) {

    init {
        adapter.onErrorAction = { refresh(true) }
        adapter.onLoadMore = { load() }
    }

    fun load() {
        adapter.loading = true
        onLoad()
    }

    fun refresh(showLoading: Boolean = false) {
        adapter.loading = showLoading
        onRefresh()
    }

    protected abstract fun onLoad()
    protected abstract fun onRefresh()


    protected fun onLoaded() {
        adapter.loading = false
        onLoaded?.invoke()
        if (adapter.getCount() == 0) adapter.mode = ELEAdapter.MODE_EMPTY
    }

    fun onLoadFailed(error: String) {
        adapter.loading = false
        onLoadFailed?.invoke(error)
        if (adapter.getCount() == 0) adapter.mode = ELEAdapter.MODE_ERROR
    }

    var onLoaded: (() -> Unit)? = null
    var onLoadFailed: ((String) -> Unit)? = null


}

class MLHandler<T : Any>(eleListAdapter: ELEListAdapter<T, *>,
                         var loader: Loader<out T>,
                         val limit: Int = 20)
    : LoadingHandler<ELEListAdapter<T, *>>(eleListAdapter) {


    override fun onLoad() {
        loader.load(limit, {
            adapter.addAll(it)
            onLoaded()
            loader.incSkip(limit)
        }, {
            onLoadFailed(it)
        })
    }

    override fun onRefresh() {
        loader.reset()
        loader.load(limit, {
            adapter.setAll(it)
            onLoaded()
            loader.incSkip(limit)
        }, {
            onLoadFailed(it)
        })
    }


}