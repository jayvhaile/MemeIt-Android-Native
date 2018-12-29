package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.models.Tag

class UserTagLoader(private val uid: String) : TagLoader, Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Tag>) -> Unit, onError: (String) -> Unit) {
        MemeItUsers.getTagsFor(uid, skip, limit).call(onSuccess, onError)
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