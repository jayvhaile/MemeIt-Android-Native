package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag

class SuggestedTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this()
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Tag>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getSuggestedTags(skip, limit).call(onSuccess,onError)
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