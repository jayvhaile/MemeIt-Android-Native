package com.innov8.memeit.loaders

import android.os.Parcelable
import com.memeit.backend.models.*

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

interface MemeLoader<T : HomeElement> : Loader<T>, Parcelable
interface UserListLoader : Loader<User>, Parcelable



interface TagLoader : Loader<Tag>, Parcelable {
    companion object {
        val loaders = listOf(MyTagLoader, PopularTagLoader, TrendingTagLoader)
    }
}