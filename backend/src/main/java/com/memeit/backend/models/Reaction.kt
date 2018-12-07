package com.memeit.backend.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Jv on 6/29/2018.
 */
data class Reaction(private var type: Int = 0,
                    @SerializedName("mid") var memeID: String? = null,
                    @SerializedName("rid") val reactorID: String? = null,
                    val poster: Poster? = null) : Parcelable {


    enum class ReactionType(val score: Float) {
        FUNNY(2f), VERY_FUNNY(3f), STUPID(-1f), ANGERING(-2f);

        fun create(mid: String?=null, rid: String? = null, poster: Poster? = null) =
                Reaction(ordinal, mid, rid, poster)
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Poster::class.java.classLoader))

    fun getType(): ReactionType {
        return ReactionType.values()[type]
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(memeID)
        parcel.writeString(reactorID)
        parcel.writeParcelable(poster, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reaction> {
        override fun createFromParcel(parcel: Parcel): Reaction {
            return Reaction(parcel)
        }

        override fun newArray(size: Int): Array<Reaction?> {
            return arrayOfNulls(size)
        }
    }
}

data class ReactionGroup(private val type: Int = 0, var count: Int = 0) {
    fun getType(): Reaction.ReactionType {
        return Reaction.ReactionType.values()[type]
    }
}
