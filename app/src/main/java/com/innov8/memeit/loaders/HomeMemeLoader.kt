package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.innov8.memeit.utils.AdElement
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.utils.*
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import retrofit2.Call
import retrofit2.Response

class HomeMemeLoader() : MemeLoader<HomeElement> {
    private val frc = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettings(FirebaseRemoteConfigSettings.Builder().build())
        setDefaults(getDefaults())
        fetch().addOnSuccessListener {
            activateFetched()
        }.addOnFailureListener {
            log(it.message ?: "")

        }
    }
    private val offsets = listOf(
            RCK_USER_SUG_OFFSET,
            RCK_TAG_SUG_OFFSET,
            RCK_TEMPLATE_SUG_OFFSET,
            RCK_AD_OFFSET
    ).map { frc.getLong(it) }
    private val periods = listOf(
            RCK_USER_SUG_PERIOD,
            RCK_TAG_SUG_PERIOD,
            RCK_TEMPLATE_SUG_PERIOD,
            RCK_AD_PERIOD
    ).map { frc.getLong(it) }
    override var skip: Int = 0
    private var userSuggestions: List<User>? = null
    private var tagsSuggestions: List<Tag>? = null
    private var memeTemplates: List<MemeTemplate>? = null
    private var usersLoaded = false
    private var tagsLoaded = false
    private var templateLoaded = false
    private var index = 0
    var type = 0
    private var usersIndex = 0
    private var tagsIndex = 0
    private var templateIndex = 0

    data class Result<T>(val body: T?, val error: String = "") {
        val isSuccessful get() = body != null

        constructor(err: String?) : this(null, err ?: "")
        constructor(res: Response<T>) : this(res.body(), res.errorBody()?.string() ?: res.message())
    }

    private fun <T> Call<T>.execSafe(): Result<T> =
            try {
                this.execute().toResult()
            } catch (e: Exception) {
                Result(e.message)
            }

    private fun <T> Response<T>.toResult() = Result(this)
    override fun load(limit: Int, onSuccess: (List<HomeElement>) -> Unit, onError: (String) -> Unit) {

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val memes = withContext(Dispatchers.Default) { MemeItMemes.getHomeMemes(skip, limit).execSafe() }
            val users = async(Dispatchers.Default) { MemeItUsers.getUserSuggestions().execSafe() }
            val tags = async(Dispatchers.Default) { MemeItMemes.getSuggestedTags(0, 100).execSafe() }
            val templates = async(Dispatchers.Default) { MemeItMemes.getTemplates(0, 100,sort = Sorter.RECENT.name).execSafe() }

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
                    val r = templates.await()
                    if (r.isSuccessful) {
                        memeTemplates = r.body
                        templateLoaded = true
                    }
                }
                onSuccess(withContext(Dispatchers.Default) {
                    val homeElements = mutableListOf<HomeElement>()
                    memes.body?.forEach {
                        homeElements.add(it)
                        index++
                        bakersList.forEachIndexed { i, baker ->
                            if ((index - offsets[i]) % periods[i] == 0L)
                                baker.invoke()?.let { elem ->
                                    homeElements.add(elem)
                                }
                        }
                    }
                    homeElements
                })
            }

        }
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
            val m = MemeTemplateSuggestion(templates.subList(templateIndex, templateIndex + rem))
            templateIndex += rem
            m
        } else null
    }

    private fun bakeAD(): AdElement? {
        return AdElement(MemeItApp.instance)
    }

    override fun reset() {
        super.reset()
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