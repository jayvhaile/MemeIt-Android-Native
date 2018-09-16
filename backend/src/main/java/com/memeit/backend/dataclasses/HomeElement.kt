package com.memeit.backend.dataclasses

const val MEME_TYPE=0
const val USER_SUGGESTION_TYPE=1
const val TAG_SUGGESTION_TYPE=2
const val MEME_TEMPLATE_SUGGESTION_TYPE=3
const val AD_TYPE=4
interface HomeElement{
    val itemType:Int
}


class UserSuggestion(val users:List<User>): HomeElement {
    override val itemType: Int= USER_SUGGESTION_TYPE
}
class TagSuggestion(val tags:List<Tag>): HomeElement {
    override val itemType: Int= TAG_SUGGESTION_TYPE
}
class MemeTemplateSuggestion(val templates:List<String>): HomeElement {
    override val itemType: Int= MEME_TEMPLATE_SUGGESTION_TYPE
}
class AdElement : HomeElement {
    override val itemType: Int= AD_TYPE
}

/*class Meme : Parcelable {

    @SerializedName("mid")
    var memeId: String? = null
        private set
    @SerializedName("poster")
    var poster: Poster? = null
        private set
    @SerializedName("img_url")
    var memeImageUrl: String? = null
        private set
    @SerializedName("ratio")
    private var memeImageRatio: Double? = null
    @SerializedName("tags")
    var tags: List<String>? = null
    @SerializedName("texts")
    var texts: List<String>? = null
    @SerializedName("date")
    var date: Long? = null
        private set
    @SerializedName("reactionCount")
    var reactionCount: Long? = null
    @SerializedName("commentCount")
    var commentCount: Long? = null
    @SerializedName("point")
    var point: Double? = null
    @SerializedName("type")
    private val type: String="image"

    enum class MemeType {
        IMAGE, GIF
    }


    constructor() {

    }

    private constructor(memeId: String) {
        this.memeId = memeId
    }

    private constructor(memeImageUrl: String, memeImageRatio: Double, type: MemeType, texts: List<String>, tags: List<String>) {
        this.memeImageUrl = memeImageUrl
        this.tags = tags
        this.texts = texts
        this.type = type.toString().toLowerCase()
        this.memeImageRatio = memeImageRatio
    }

    private constructor(texts: List<String>, tags: List<String>, mid: String) {
        this.memeId = mid
        this.tags = tags
        this.texts = texts
    }

    private constructor(memeId: String, poster: Poster, memeImageUrl: String, memeImageRatio: Double, tags: List<String>, texts: List<String>, date: Long?, reactionCount: Long?, commentCount: Long?, point: Double?) {
        this.memeId = memeId
        this.poster = poster
        this.memeImageUrl = memeImageUrl
        this.memeImageRatio = memeImageRatio
        this.tags = tags
        this.texts = texts
        this.date = date
        this.reactionCount = reactionCount
        this.commentCount = commentCount
        this.point = point
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(memeId)
        parcel.writeString(memeImageUrl)
        parcel.writeStringList(tags)
        parcel.writeStringList(texts)
        parcel.writeLong(date!!)
        parcel.writeLong(reactionCount!!)
        parcel.writeLong(commentCount!!)
        parcel.writeDouble(point!!)
        parcel.writeDouble(memeImageRatio!!)
        parcel.writeParcelable(poster, i)
    }

    protected constructor(`in`: Parcel) {
        memeId = `in`.readString()
        memeImageUrl = `in`.readString()
        tags = `in`.createStringArrayList()
        texts = `in`.createStringArrayList()
        date = `in`.readLong()
        reactionCount = `in`.readLong()
        commentCount = `in`.readLong()
        point = `in`.readDouble()
        memeImageRatio = `in`.readDouble()
        poster = `in`.readParcelable(Poster::class.java.classLoader)
    }

    fun getType(): MemeType {
        return MemeType.valueOf(type.toUpperCase())
    }

    fun getMemeImageRatio(): Double {
        return if (memeImageRatio == null) 1.0 else memeImageRatio
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is Meme) {
            val meme = obj as Meme?
            return this.memeId == meme!!.memeId
        }
        return false
    }

    override fun hashCode(): Int {
        return this.memeId!!.hashCode()
    }

    protected override fun copy(): Meme {
        val meme = Meme()
        meme.memeId = memeId
        meme.poster = poster
        meme.memeImageUrl = memeImageUrl
        meme.tags = tags
        meme.texts = texts
        meme.date = date
        meme.reactionCount = reactionCount
        meme.commentCount = commentCount
        meme.point = point
        meme.memeImageRatio = memeImageRatio
        return meme
    }

    fun forUpdate(texts: List<String>, tags: List<String>): Meme {
        return Meme(texts, tags, memeId)
    }


    fun makeReaction(reactionType: Reaction.ReactionType): Reaction {
        return Reaction.create(reactionType, memeId)
    }

    fun makeComment(comment: String): Comment {
        return Comment.createComment(memeId, comment)
    }

    fun refresh(meme: Meme): Meme {
        val m = clone()
        m.commentCount = meme.commentCount
        m.reactionCount = meme.reactionCount
        m.point = meme.point
        return this
    }

    companion object {


        fun createMeme(memeImageUrl: String, memeImageRatio: Double, type: MemeType): Meme {
            return Meme(memeImageUrl, memeImageRatio, type, ArrayList(), ArrayList())
        }

        fun createMeme(memeImageUrl: String, memeImageRatio: Double, type: MemeType, texts: List<String>): Meme {
            return Meme(memeImageUrl, memeImageRatio, type, texts, ArrayList())
        }

        fun createMeme(memeImageUrl: String, memeImageRatio: Double, type: MemeType, texts: List<String>, tags: List<String>): Meme {
            return Meme(memeImageUrl, memeImageRatio, type, texts, tags)
        }

        fun forID(memeID: String): Meme {
            return Meme(memeID)
        }

        val CREATOR: Parcelable.Creator<Meme> = object : Parcelable.Creator<Meme> {
            override fun createFromParcel(`in`: Parcel): Meme {
                return Meme(`in`)
            }

            override fun newArray(size: Int): Array<Meme> {
                return arrayOfNulls(size)
            }
        }
    }

}*/


