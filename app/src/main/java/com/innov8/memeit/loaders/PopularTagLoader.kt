package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag

class PopularTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this()

    var search: String? = null

    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Tag>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getPopularTags(search, skip, limit).call(onSuccess, onError)
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