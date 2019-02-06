package com.memeit.backend.models

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

/**
 * Created by Jv on 5/13/2018.
 */
data class Comment(
        @SerializedName("cid") var id: String? = null,
        @SerializedName("mid") var memeID: String? = null,
        @SerializedName("poster") var poster: User? = null,
        @SerializedName("comment") var comment: String? = null,
        @SerializedName("date") var date: Long? = null,
        @SerializedName("replyCount") var replyCount: Long = 0,
        @SerializedName("likeCount") var likeCount: Long = 0,
        @SerializedName("dislikeCount") var dislikeCount: Long = 0,
        @SerializedName("isLikedByMe") var isLikedByMe: Boolean = false,
        @SerializedName("isDislikedByMe") var isDislikedByMe: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(User::class.java.classLoader),
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(memeID)
        parcel.writeParcelable(poster, flags)
        parcel.writeString(comment)
        parcel.writeValue(date)
        parcel.writeLong(replyCount)
        parcel.writeLong(likeCount)
        parcel.writeLong(dislikeCount)
        parcel.writeByte(if (isLikedByMe) 1 else 0)
        parcel.writeByte(if (isDislikedByMe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}
