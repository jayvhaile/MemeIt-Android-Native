package com.memeit.backend.dataclasses

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Jv on 4/29/2018.
 */
data class User(var uid: String? = null,
                var name: String? = null,
                var bio: String? = null,
                var username: String? = null,
                @SerializedName("pic") var imageUrl: String? = null,
                @SerializedName("cpic")var coverImageUrl: String? = null,
                var followerCount: Int = 0,
                var followingCount: Int = 0,
                var postCount: Int = 0,
                var isFollowingMe: Boolean = false,
                var isFollowedByMe: Boolean = false) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte())
    constructor(poster: Poster):this(uid=poster.id,name=poster.name,imageUrl = poster.profileUrl)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(bio)
        parcel.writeString(username)
        parcel.writeString(imageUrl)
        parcel.writeString(coverImageUrl)
        parcel.writeInt(followerCount)
        parcel.writeInt(followingCount)
        parcel.writeInt(postCount)
        parcel.writeByte(if (isFollowingMe) 1 else 0)
        parcel.writeByte(if (isFollowedByMe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
