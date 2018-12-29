package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme

class TagMemeLoader(val tag:String) : MemeLoader<Meme> {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun load(limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getFilteredMemes(null, arrayOf(tag),skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagMemeLoader> {
        override fun createFromParcel(parcel: Parcel): TagMemeLoader {
            return TagMemeLoader(parcel)
        }

        override fun newArray(size: Int): Array<TagMemeLoader?> {
            return arrayOfNulls(size)
        }
    }
}