package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Tag

class UserMemePostsLoader(private val userID: String?) : MemeLoader<Meme> {

    constructor(parcel: Parcel) : this(parcel.readString())

    override var skip: Int = 0

    override fun load(limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        if (userID == null)
            MemeItMemes.getMyMemes(skip, limit).call(onSuccess, onError)
        else
            MemeItMemes.getMemesFor(userID, skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserMemePostsLoader> {
        override fun createFromParcel(parcel: Parcel): UserMemePostsLoader {
            return UserMemePostsLoader(parcel)
        }

        override fun newArray(size: Int): Array<UserMemePostsLoader?> {
            return arrayOfNulls(size)
        }
    }
}