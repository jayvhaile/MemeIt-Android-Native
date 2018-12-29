package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme

class GuestMemeLoader() : MemeLoader<Meme> {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this()

    override fun load(limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getTrendingMemesForGuest(skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GuestMemeLoader> {
        override fun createFromParcel(parcel: Parcel): GuestMemeLoader {
            return GuestMemeLoader(parcel)
        }

        override fun newArray(size: Int): Array<GuestMemeLoader?> {
            return arrayOfNulls(size)
        }
    }
}