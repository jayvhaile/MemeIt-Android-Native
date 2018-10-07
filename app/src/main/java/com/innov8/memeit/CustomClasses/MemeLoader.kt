package com.innov8.memeit.CustomClasses

import android.os.Parcel
import android.os.Parcelable
import com.facebook.common.util.UriUtil
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.getDrawableIdByName
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.trim
import com.memeit.backend.dataclasses.*
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.call
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

interface Loader<T>{
    fun load(skip: Int, limit: Int, onSuccess: (List<T>) -> Unit, onError: (String) -> Unit = {})
    fun reset() {}
}

interface MemeLoader<T:HomeElement> : Loader<T>,Parcelable
interface UserListLoader:Loader<User>,Parcelable

class EmptyLoader<T>() : Loader<T>{
    constructor(parcel: Parcel) : this()
    override fun load(skip: Int, limit: Int, onSuccess: (List<T>) -> Unit, onError: (String) -> Unit) {
        onSuccess(listOf())
    }
}

class HomeLoader() : MemeLoader<HomeElement> {
    private var userSuggestions: List<User>? = null
    private var tagsSuggestions: List<Tag>? = null
    private var memeTemplates: List<MemeTemplate>? = null

    private var usersLoaded = false
    private var tagsLoaded = false
    private var templateLoaded = false



    var index = 0
    val offset = 2
    var type = 0
    private var usersIndex = 0
    private var tagsIndex = 0
    private var templateIndex = 0

    override fun load(skip: Int, limit: Int, onSuccess: (List<HomeElement>) -> Unit, onError: (String) -> Unit) {
        launch(UI) {
            val users = async(CommonPool) { MemeItUsers.getUserSuggestions().execute() }
            val tags = async(CommonPool) { MemeItMemes.getSuggestedTags(0, 300).execute() }
            val templates = async(CommonPool) { MemeTemplate.loadLocalTemplates(MemeItApp.instance) }
            val memes = async(CommonPool) { MemeItMemes.getHomeMemes(skip, limit).execute() }.await()

            if (!memes.isSuccessful) {
                onError(memes.errorBody()?.string() ?: memes.message())
            } else {
                if (!usersLoaded) {
                    val r = users.await()
                    if (r.isSuccessful) {
                        userSuggestions = r.body()
                        usersLoaded = true
                    }
                }
                if (!tagsLoaded) {
                    val r = tags.await()
                    if (r.isSuccessful) {
                        tagsSuggestions = r.body()
                        tagsLoaded = true
                    }
                }
                if (!templateLoaded) {
                    memeTemplates = templates.await()
                    templateLoaded = true
                }
                if (memes.isSuccessful)
                    onSuccess(async(CommonPool) {
                        val homeElements = mutableListOf<HomeElement>()
                        memes.body()?.forEach {
                            homeElements.add(it)
                            index++
                            if (index % offset == 0) {
                                val x = loadHorizontalHomeElement()
                                if (x != null) {
                                    homeElements.add(x)
                                }
                            }
                        }
                        homeElements
                    }.await())
            }

        }
    }

    private fun loadHorizontalHomeElement(): HomeElement? {
        var horizontalElement: HomeElement? = null
        val s = type % bakersList.size
        while (horizontalElement == null && s < s + bakersList.size) {
            horizontalElement = bakersList[type % bakersList.size]()
            type++
        }
        return horizontalElement
    }

    private val bakersList = listOf(
            { bakeSuggestion() },
            { bakeTags() },
            { bakeTemplate() },
            { bakeAD() }
    )

    constructor(parcel: Parcel) : this() {
        userSuggestions = parcel.createTypedArrayList(User.CREATOR)
        usersLoaded = parcel.readByte() != 0.toByte()
        tagsLoaded = parcel.readByte() != 0.toByte()
        templateLoaded = parcel.readByte() != 0.toByte()
        index = parcel.readInt()
        type = parcel.readInt()
        usersIndex = parcel.readInt()
        tagsIndex = parcel.readInt()
        templateIndex = parcel.readInt()
    }

    private fun bakeSuggestion(): UserSuggestion? {
        val users = userSuggestions ?: return null
        val rem = (users.size - usersIndex) trim 10
        return if (rem > 2) {
            val u = UserSuggestion(users.subList(usersIndex, usersIndex + rem))
            usersIndex += rem
            u
        } else null
    }

    private fun bakeTags(): TagSuggestion? {
        val tags = tagsSuggestions ?: return null
        val rem = (tags.size - tagsIndex) trim 10
        return if (rem > 2) {
            val t = TagSuggestion(tags.subList(tagsIndex, tagsIndex + rem))
            tagsIndex += rem
            t
        } else null
    }

    private fun bakeTemplate(): MemeTemplateSuggestion? {
        val templates = memeTemplates ?: return null
        val rem = (templates.size - templateIndex) trim 10
        return if (rem > 2) {
            val m = MemeTemplateSuggestion(templates.subList(templateIndex, templateIndex + rem).map {
                if (it.dataSource == MemeTemplate.LOCAL_DATA_SOURCE) {
                    UriUtil.getUriForResourceId(MemeItApp.instance.getDrawableIdByName(it.imageURL)).toString()
                } else {
                    ""      //todo handle
                }
            })
            templateIndex += rem
            m
        } else null
    }

    private fun bakeAD(): AdElement? {
        return AdElement()
    }

    override fun reset() {
        userSuggestions = null
        tagsSuggestions = null
        memeTemplates = null
        usersLoaded = false
        tagsLoaded = false
        templateLoaded = false
        index = 0
        type = 0
        templateIndex = 0
        usersIndex = 0
        tagsIndex = 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(userSuggestions)
        parcel.writeByte(if (usersLoaded) 1 else 0)
        parcel.writeByte(if (tagsLoaded) 1 else 0)
        parcel.writeByte(if (templateLoaded) 1 else 0)
        parcel.writeInt(index)
        parcel.writeInt(type)
        parcel.writeInt(usersIndex)
        parcel.writeInt(tagsIndex)
        parcel.writeInt(templateIndex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeLoader> {
        override fun createFromParcel(parcel: Parcel): HomeLoader {
            return HomeLoader(parcel)
        }

        override fun newArray(size: Int): Array<HomeLoader?> {
            return arrayOfNulls(size)
        }
    }

}
class SearchLoader(var search: String, var tags: Array<String>) : MemeLoader<Meme> {


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

    companion object CREATOR : Parcelable.Creator<SearchLoader> {
        override fun createFromParcel(parcel: Parcel): SearchLoader {
            return SearchLoader(parcel)
        }

        override fun newArray(size: Int): Array<SearchLoader?> {
            return arrayOfNulls(size)
        }
    }
}
class TrendingLoader() : MemeLoader<Meme> {
    constructor(parcel: Parcel) : this()

    override fun load(skip: Int, limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getTrendingMemes(skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrendingLoader> {
        override fun createFromParcel(parcel: Parcel): TrendingLoader {
            return TrendingLoader(parcel)
        }

        override fun newArray(size: Int): Array<TrendingLoader?> {
            return arrayOfNulls(size)
        }
    }
}
class FavoritesLoader() : MemeLoader<Meme> {
    constructor(parcel: Parcel) : this()

    override fun load(skip: Int, limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getFavouriteMemes(skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoritesLoader> {
        override fun createFromParcel(parcel: Parcel): FavoritesLoader {
            return FavoritesLoader(parcel)
        }

        override fun newArray(size: Int): Array<FavoritesLoader?> {
            return arrayOfNulls(size)
        }
    }

}
class MyMemesLoader(private val userID: String?) : MemeLoader<Meme> {

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun load(skip: Int, limit: Int, onSuccess: (List<Meme>) -> Unit, onError: (String) -> Unit) {
        if (userID == null)
            MemeItMemes.getMyMemes(skip, limit).call(onSuccess, onError)
        else
            MemeItMemes.getMemesFor(userID, skip, limit).call(onSuccess, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyMemesLoader> {
        override fun createFromParcel(parcel: Parcel): MyMemesLoader {
            return MyMemesLoader(parcel)
        }

        override fun newArray(size: Int): Array<MyMemesLoader?> {
            return arrayOfNulls(size)
        }
    }
}



class FollowerLoader(private val uid: String?) : UserListLoader {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun load(skip: Int, limit: Int, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        if (uid == null)
            MemeItUsers.getMyFollowersList(skip, limit).call(onSuccess,onError)
        else
            MemeItUsers.getFollowersListForUser(uid, skip, limit).call(onSuccess,onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowerLoader> {
        override fun createFromParcel(parcel: Parcel): FollowerLoader {
            return FollowerLoader(parcel)
        }

        override fun newArray(size: Int): Array<FollowerLoader?> {
            return arrayOfNulls(size)
        }
    }
}
class FollowingLoader(private val uid: String?) : UserListLoader {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun load(skip: Int, limit: Int, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        if (uid == null)
            MemeItUsers.getMyFollowingList(skip, limit).call(onSuccess,onError)
        else
            MemeItUsers.getFollowingListForUser(uid, skip, limit).call(onSuccess,onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowingLoader> {
        override fun createFromParcel(parcel: Parcel): FollowingLoader {
            return FollowingLoader(parcel)
        }

        override fun newArray(size: Int): Array<FollowingLoader?> {
            return arrayOfNulls(size)
        }
    }
}