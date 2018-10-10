package com.innov8.memeit.Loaders

import android.os.Parcelable
import com.memeit.backend.dataclasses.*
import retrofit2.Call

interface Loader<T> {
    fun load(skip: Int, limit: Int, onSuccess: (List<T>) -> Unit, onError: (String) -> Unit = {})
    fun reset() {}
}

interface MemeLoader<T : HomeElement> : Loader<T>, Parcelable
interface UserListLoader : Loader<User>, Parcelable


interface TagLoader : Parcelable {
    companion object {
        val loaders = listOf(MyTagLoader, PopularTagLoader, TrendingTagLoader)
    }

    fun loadTags(skip: Int, limit: Int): Call<List<Tag>>
}