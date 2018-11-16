package com.memeit.backend.dataclasses

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.innov8.memeit.commons.getDrawableIdByName

data class Badge(val id: String,
                 val label: String = "label",
                 @SerializedName("desc") val description: String,
                 val level: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(label)
        parcel.writeString(description)
        parcel.writeInt(level)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getDrawableId(context: Context) = context.getDrawableIdByName(id)
    override fun equals(other: Any?): Boolean {
        return (other as? Badge)?.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<Badge> {
        override fun createFromParcel(parcel: Parcel): Badge {
            return Badge(parcel)
        }

        override fun newArray(size: Int): Array<Badge?> {
            return arrayOfNulls(size)
        }

        fun ofID(id: String) = allBadges.find { it.id == id } ?: Badge(
                "none",
                "None",
                "none",
                0
        )

        val allBadges by lazy {
            listOf(
                    Badge(
                            "first_001",
                            "One small step for a memer",
                            "Upload your first Meme",
                            1
                    ),
                    Badge(
                            "first_002",
                            "Baby steps ",
                            "Get your first reaction",
                            2
                    ),
                    Badge(
                            "first_003",
                            "Silence is not a virtue",
                            "Get your first comment",
                            2
                    ),
                    Badge(
                            "react_001",
                            "Stoic audience",
                            "Get 10 reactions",
                            3
                    ),
                    Badge(
                            "react_002",
                            "Funny content",
                            "Get 100 reactions",
                            5
                    ),
                    Badge(
                            "react_003",
                            "Daymaker",
                            "Get 1k reactions",
                            7
                    ),
                    Badge(
                            "react_004",
                            " ",
                            "Get 10k reactions",
                            9
                    ),
                    Badge(
                            "react_005",
                            "Worldwide phenomenon",
                            "Get 100k reactions",
                            11
                    ),
                    Badge(
                            "comment_001",
                            "Spectator",
                            "Get 10 Comments",
                            3
                    ),
                    Badge(
                            "comment_002",
                            "Commentator",
                            "Get 100 Comments",
                            5
                    ),
                    Badge(
                            "comment_003",
                            "Commentary genius",
                            "Get 1k Comments",
                            7
                    ),
                    Badge(
                            "comment_004",
                            "A pile of comments",
                            "Get 10k Comments",
                            9
                    ),
                    Badge(
                            "comment_005",
                            "Comment horde! Incoming!",
                            "Get 100k Comments",
                            11
                    ),
                    Badge(
                            "follower_001",
                            "You are getting there",
                            "Get 10 Followers",
                            4
                    ),
                    Badge(
                            "follower_002",
                            "Hey everyone!",
                            "Get 100 Followers",
                            6
                    ),
                    Badge(
                            "follower_003",
                            "Follower Magnet",
                            "Get 1k Followers",
                            8
                    ),
                    Badge(
                            "follower_004",
                            "Mr Popular ",
                            "Get 10k Followers",
                            10
                    ),
                    Badge(
                            "follower_005",
                            "You've got a cult following",
                            "Get 100k Followers",
                            12
                    ),
                    Badge(
                            "trending_001",
                            " ",
                            "Get Meme in Top 10 Trending",
                            4
                    ),
                    Badge(
                            "trending_002",
                            " ",
                            "Get Meme in Top 5 Trending",
                            7
                    ),
                    Badge(
                            "trending_003",
                            " ",
                            "Get Meme in Top 3 Trending",
                            10
                    ),
                    Badge(
                            "invite_001",
                            "Let the word spread",
                            "Invite 10 Users",
                            1
                    ),
                    Badge(
                            "invite_002",
                            "You and your friends",
                            "Invite 50 Users",
                            2
                    ),
                    Badge(
                            "invite_003",
                            "Your whole neighborhood",
                            "Invite 100 Users",
                            3
                    ),
                    Badge(
                            "invite_004",
                            "Worth of praise",
                            "Invite 500 Users",
                            4
                    ),
                    Badge(
                            "invite_005",
                            "We owe you one!",
                            "Invite 1000 Users",
                            4
                    )
            )
        }


    }
}
