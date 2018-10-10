package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Tag
import retrofit2.Call

class SuggestedTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItMemes.getSuggestedTags(skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SuggestedTagLoader> {
        override fun createFromParcel(parcel: Parcel): SuggestedTagLoader {
            return SuggestedTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<SuggestedTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}