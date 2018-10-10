package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.Tag
import retrofit2.Call

class UserTagLoader(private val uid: String) : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun loadTags(skip: Int, limit: Int): Call<List<Tag>> {
        return MemeItUsers.getTagsFor(uid, skip, limit)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserTagLoader> {
        override fun createFromParcel(parcel: Parcel): UserTagLoader {
            return UserTagLoader(parcel)
        }

        override fun newArray(size: Int): Array<UserTagLoader?> {
            return arrayOfNulls(size)
        }
    }
}