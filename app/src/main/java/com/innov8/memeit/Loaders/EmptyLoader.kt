package com.innov8.memeit.Loaders

import android.os.Parcel

class EmptyLoader<T>() : Loader<T> {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this()

    override fun load(limit: Int, onSuccess: (List<T>) -> Unit, onError: (String) -> Unit) {
        onSuccess(listOf())
    }
}