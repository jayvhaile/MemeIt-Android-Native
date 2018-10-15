package com.innov8.memeit.Loaders

import android.os.Parcel
import android.os.Parcelable
import com.innov8.memegenerator.Models.MemeTemplate
import com.innov8.memeit.CustomClasses.AdElement
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.trim
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.Call
import retrofit2.Response

class HomeMemeLoader() : MemeLoader<HomeElement> {
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

    data class Result<T>(val body: T?, val error: String = "") {
        val isSuccessful get() = body != null

        constructor(err: String?) : this(null, err ?: "")
        constructor(res: Response<T>) : this(res.body(), res.errorBody()?.string() ?: res.message())
    }

    fun <T> Call<T>.execSafe(): Result<T> =
            try {
                this.execute().toResult()
            } catch (e: Exception) {
                Result(e.message)
            }


    fun <T> Response<T>.toResult() = Result(this)

    override fun load(skip: Int, limit: Int, onSuccess: (List<HomeElement>) -> Unit, onError: (String) -> Unit) {
        launch(UI) {
            val memes = withContext(CommonPool) { MemeItMemes.getHomeMemes(skip, limit).execSafe() }
            val users = async(CommonPool) { MemeItUsers.getUserSuggestions().execSafe() }
            val tags = async(CommonPool) { MemeItMemes.getSuggestedTags(0, 300).execSafe() }
            val templates = async(CommonPool) { MemeTemplate.loadLocalTemplates(MemeItApp.instance) }

            if (!memes.isSuccessful) {
                onError(memes.error)
            } else {
                if (!usersLoaded) {
                    val r = users.await()
                    if (r.isSuccessful) {
                        userSuggestions = r.body
                        usersLoaded = true
                    }
                }
                if (!tagsLoaded) {
                    val r = tags.await()
                    if (r.isSuccessful) {
                        tagsSuggestions = r.body
                        tagsLoaded = true
                    }
                }
                if (!templateLoaded) {
                    memeTemplates = templates.await()
                    templateLoaded = true
                }
                onSuccess(withContext(CommonPool) {
                    val homeElements = mutableListOf<HomeElement>()
                    memes.body?.forEach {
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
                })
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
        return null
        /*val templates = memeTemplates ?: return null
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
        } else null*/
    }

    private fun bakeAD(): AdElement? {
        return AdElement(MemeItApp.instance)
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

    companion object CREATOR : Parcelable.Creator<HomeMemeLoader> {
        override fun createFromParcel(parcel: Parcel): HomeMemeLoader {
            return HomeMemeLoader(parcel)
        }

        override fun newArray(size: Int): Array<HomeMemeLoader?> {
            return arrayOfNulls(size)
        }
    }

}