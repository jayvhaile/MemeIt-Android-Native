package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme

class SearchMemeLoader() : MemeLoader<Meme> {
    override var skip: Int = 0
    var search: String = ""
    var tags: Array<String> = arrayOf()

    constructor(parcel: Parcel) : this() {
        search = parcel.readString()!!
        tags = parcel.createStringArray() ?: arrayOf()
    }

    override fun load(limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
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