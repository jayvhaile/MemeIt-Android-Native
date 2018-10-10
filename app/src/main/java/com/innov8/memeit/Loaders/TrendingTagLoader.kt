package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Tag
import retrofit2.Call

class TrendingTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItMemes.getTrendingTags(skip, limit)
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