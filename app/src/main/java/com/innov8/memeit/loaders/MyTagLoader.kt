package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag

class MyTagLoader() : TagLoader, Parcelable {
    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Tag>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getMyTags(skip, limit).call(onSuccess,onError)
    }

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyTagLoader> {
        override fun createFromParcel(parcel: Parcel): MyTagLoader {
            return MyTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<MyTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}