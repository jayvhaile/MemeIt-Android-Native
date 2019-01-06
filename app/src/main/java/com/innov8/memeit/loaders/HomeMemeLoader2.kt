package com.innov8.memeit.loaders

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.utils.*
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.HomeElement
import com.memeit.backend.models.MemeTemplateSuggestion
import com.memeit.backend.models.TagSuggestion
import com.memeit.backend.models.UserSuggestion
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class HomeMemeLoader2() : MemeLoader<HomeElement> {
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


    override fun load(limit: Int, onSuccess: (List<HomeElement>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getHomeFullMemes(
                skip,
                limit,
                offsets[0].toInt(),
                offsets[1].toInt(),
                offsets[2].toInt(),
                periods[0].toInt(),
                periods[1].toInt(),
                periods[2].toInt()
        ).call({ (userSug, tagSug, templateSug, memes) ->

            var usersIndex = 0
            var tagsIndex = 0
            var templateIndex = 0
            val bakersList = listOf(
                    {
                        userSug?.let { users ->
                            ((users.size - usersIndex) trim 10).takeIf { it > 2 }
                                    ?.let { rem ->
                                        UserSuggestion(users.subList(usersIndex, usersIndex + rem)).also {
                                            usersIndex += rem
                                        }
                                    }
                        }

                    },
                    {
                        tagSug?.let { tags ->
                            ((tags.size - tagsIndex) trim 10).takeIf { it > 2 }
                                    ?.let { rem ->
                                        TagSuggestion(tags.subList(tagsIndex, tagsIndex + rem)).also {
                                            tagsIndex += rem
                                        }
                                    }
                        }

                    },
                    {
                        templateSug?.let { templates ->
                            ((templates.size - templateIndex) trim 10).takeIf { it > 2 }
                                    ?.let { rem ->
                                        MemeTemplateSuggestion(templates.subList(templateIndex, templateIndex + rem))
                                                .also {
                                                    templateIndex += rem
                                                }
                                    }
                        }

                    },
                    { AdElement(MemeItApp.instance) }
            )
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                onSuccess(withContext(Dispatchers.Default) {
                    val homeElements = mutableListOf<HomeElement>()
                    memes?.forEachIndexed { index, meme ->
                        homeElements.add(meme)
                        bakersList.forEachIndexed { i, baker ->
                            if (((skip + index + 1) - offsets[i]) % periods[i] == 0L) {
                                baker()?.let { homeElements.add(it) }
                            }
                        }
                    }
                    homeElements
                })

            }

        }, onError)

    }


    constructor(parcel: Parcel) : this() {
        skip = parcel.readInt()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(skip)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeMemeLoader2> {
        override fun createFromParcel(parcel: Parcel): HomeMemeLoader2 {
            return HomeMemeLoader2(parcel)
        }

        override fun newArray(size: Int): Array<HomeMemeLoader2?> {
            return arrayOfNulls(size)
        }
    }

}