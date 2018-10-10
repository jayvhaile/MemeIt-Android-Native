package com.innov8.memeit.Loaders

import android.os.Parcel

class EmptyLoader<T>() : Loader<T> {
    constructor(parcel: Parcel) : this()

    override fun load(skip: Int, limit: Int, onSuccess: (List<T>) -> Unit, onError: (String) -> Unit) {
        onSuccess(listOf())
    }
}