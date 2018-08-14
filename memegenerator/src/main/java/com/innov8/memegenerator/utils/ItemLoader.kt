package com.innov8.memegenerator.utils

interface Loader<T> {
    fun load(): T
}

class AsyncLoader<T>(val task: () -> T) {
    fun load(onFinished: (T) -> Unit) {
        MyAsyncTask<T>().start {
            return@start task()
        }.onFinished(onFinished)
    }
}

