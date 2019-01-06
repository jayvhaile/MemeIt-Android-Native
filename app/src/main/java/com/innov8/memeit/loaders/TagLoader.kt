package com.innov8.memeit.loaders

import android.os.Parcelable
import com.innov8.memeit.commons.Loader
import com.memeit.backend.models.Tag

interface TagLoader : Loader<Tag>, Parcelable {
    companion object {
        val loaders = listOf(MyTagLoader, PopularTagLoader, TrendingTagLoader)
    }
}