package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme

class SearchMemeLoader(var search: String, var tags: Array<String>) : MemeLoader<Meme> {


    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.createStringArray() ?: arrayOf())

    override fun load(skip: Int, limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getFilteredMemes(search, tags, skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(search)
        parcel.writeStringArray(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchMemeLoader> {
        override fun createFromParcel(parcel: Parcel): SearchMemeLoader {
            return SearchMemeLoader(parcel)
        }

        override fun newArray(size: Int): Array<SearchMemeLoader?> {
            return arrayOfNulls(size)
        }
    }
}