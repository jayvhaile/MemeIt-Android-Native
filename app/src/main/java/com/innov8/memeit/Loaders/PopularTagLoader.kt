package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Tag
import retrofit2.Call

class PopularTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItMemes.getPopularTags(null, skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PopularTagLoader> {
        override fun createFromParcel(parcel: Parcel): PopularTagLoader {
            return PopularTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<PopularTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}