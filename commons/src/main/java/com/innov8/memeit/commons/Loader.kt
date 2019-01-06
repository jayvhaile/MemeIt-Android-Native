package com.innov8.memeit.commons

interface Loader<T> {
    var skip: Int
    fun load(limit: Int, onSuccess: (List<T>) -> Unit, onError: (String) -> Unit = {})
    fun reset() {
        resetSkip()
    }

    fun incSkip(size: Int) {
        skip += size
    }

    fun resetSkip() {
        skip = 0
    }

}

