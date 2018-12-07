package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag

class TrendingTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this()
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Tag>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getTrendingTags(skip, limit).call(onSuccess,onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrendingTagLoader> {
        override fun createFromParcel(parcel: Parcel): TrendingTagLoader {
            return TrendingTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<TrendingTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}