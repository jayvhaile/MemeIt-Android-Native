package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.Tag
import retrofit2.Call

class MyTagLoader() : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this()

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItUsers.getMyTags(skip, limit)
    }

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