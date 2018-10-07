package com.memeit.backend.dataclasses

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

/**
 * Created by Jv on 6/16/2018.
 */

data class Poster(@SerializedName("pid")
                  val id: String?,
                  @SerializedName("name")
                  val name: String?,
                  @SerializedName("pic")
                  val profileUrl: String?) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    fun toUser(): User {
        return User(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(profileUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Poster> {
        override fun createFromParcel(parcel: Parcel): Poster {
            return Poster(parcel)
        }

        override fun newArray(size: Int): Array<Poster?> {
            return arrayOfNulls(size)
        }
    }


}
