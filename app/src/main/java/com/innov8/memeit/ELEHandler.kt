package com.innov8.memeit

import com.innov8.memeit.Adapters.ELEAdapter
import com.innov8.memeit.Adapters.ELEListAdapter
import com.innov8.memeit.Loaders.Loader

abstract class LoadingHandler<A : ELEAdapter<*>>(val adapter: A) {

    init {
        adapter.onErrorAction = { refresh(true) }
        if (adapter.onEmptyAction == null)
            adapter.onEmptyAction = { refresh(true) }
        adapter.onLoadMore = { load() }
    }

    fun load() {
        adapter.loading = true
        onLoad(false)
    }

    fun refresh(showLoading: Boolean = false) {
        adapter.loading = showLoading
        onLoad(true)
    }

    protected abstract fun onLoad(refresh: Boolean)


    protected fun beforeLoaded() {
        adapter.loading = false
    }

    protected fun afterLoaded() {
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


    override fun onLoad(refresh: Boolean) {
        if (refresh) loader.reset()
        loader.load(limit, {
            beforeLoaded()
            if (refresh) adapter.setAll(it) else adapter.addAll(it)
            afterLoaded()
            loader.incSkip(it.size)
        }, {
            onLoadFailed(it)
        })
    }


}