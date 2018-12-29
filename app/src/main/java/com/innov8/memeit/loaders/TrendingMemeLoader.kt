package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme

class TrendingMemeLoader() : MemeLoader<Meme> {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this()

    override fun load(limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getTrendingMemes(skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrendingMemeLoader> {
        override fun createFromParcel(parcel: Parcel): TrendingMemeLoader {
            return TrendingMemeLoader(parcel)
        }

        override fun newArray(size: Int): Array<TrendingMemeLoader?> {
            return arrayOfNulls(size)
        }
    }
}