package com.memeit.backend.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Reply(
        @SerializedName("rid") val id: String? = null,
        val poster: User? = null,
        var reply: String,
        val date: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(User::class.java.classLoader),
            parcel.readString()!!,
            parcel.readValue(Long::class.java.classLoader) as? Long)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(poster, flags)
        parcel.writeString(reply)
        parcel.writeValue(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reply> {
        override fun createFromParcel(parcel: Parcel): Reply {
            return Reply(parcel)
        }

        override fun newArray(size: Int): Array<Reply?> {
            return arrayOfNulls(size)
        }
    }
}