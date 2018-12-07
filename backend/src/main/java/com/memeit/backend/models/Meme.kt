package com.memeit.backend.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Meme(@SerializedName("mid") val id: String? = null,
                @SerializedName("poster") val poster: Poster? = null,
                @SerializedName("desc") val description: String? = null,
                @SerializedName("mr") var myReaction: Reaction? = null,
                @SerializedName("img_url") val imageId: String? = null,
                @SerializedName("ratio") val imageRatio: Double = 1.0,
                @SerializedName("tags") val tags: MutableList<String> = mutableListOf(),
                @SerializedName("texts") val texts: List<String> = listOf(),
                @SerializedName("date") val date: Long? = null,
                @SerializedName("reactionCount") var reactionCount: Long = 0L,
                @SerializedName("commentCount") var commentCount: Long = 0L,
                @SerializedName("point") var point: Double = 0.0,
                @SerializedName("type") val type: String = "image",
                @SerializedName("fav") var isMyFavourite: Boolean = false) : Parcelable, HomeElement {
    override val itemType: Int
        get() = HomeElement.MEME_TYPE


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Poster::class.java.classLoader),
            parcel.readString(),
            parcel.readParcelable(Reaction::class.java.classLoader),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double ?: 1.0,
            parcel.createStringArrayList() ?: mutableListOf(),
            parcel.createStringArrayList() ?: listOf(),
            parcel.readValue(Long::class.java.classLoader) as? Long?:0L,
            parcel.readValue(Long::class.java.classLoader) as? Long?:0L,
            parcel.readValue(Long::class.java.classLoader) as? Long?:0L,
            parcel.readValue(Double::class.java.classLoader) as? Double?:0.0,
            parcel.readString() ?: "image",
            parcel.readByte() != 0.toByte())

    override fun equals(other: Any?): Boolean {
        val m2 = (other as? Meme)
        return m2?.id?.equals(id) ?: m2?.imageId?.equals(imageId) ?: false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: imageId?.hashCode() ?: super.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(poster, flags)
        parcel.writeString(description)
        parcel.writeParcelable(myReaction, flags)
        parcel.writeString(imageId)
        parcel.writeValue(imageRatio)
        parcel.writeStringList(tags)
        parcel.writeStringList(texts)
        parcel.writeValue(date)
        parcel.writeValue(reactionCount)
        parcel.writeValue(commentCount)
        parcel.writeValue(point)
        parcel.writeString(type)
        parcel.writeByte(if (isMyFavourite) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Meme> {
        override fun createFromParcel(parcel: Parcel): Meme {
            return Meme(parcel)
        }

        override fun newArray(size: Int): Array<Meme?> {
            return arrayOfNulls(size)
        }
    }

    fun getType(): MemeType = MemeType.of(type)

    enum class MemeType {
        IMAGE, GIF;

        companion object {
            fun of(s: String): MemeType {
                return valueOf(s.toUpperCase())
            }
        }
    }


}