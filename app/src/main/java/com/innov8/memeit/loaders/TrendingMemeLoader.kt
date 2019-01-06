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
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class TrendingMemeLoader() : MemeLoader<HomeElement> {
    override var skip: Int = 0

    private val frc = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettings(FirebaseRemoteConfigSettings.Builder().build())
        setDefaults(getDefaults())

    }
    private val adOffset = frc.getLong(RCK_AD_OFFSET)
    private val adPeriod = frc.getLong(RCK_AD_PERIOD)

    constructor(parcel: Parcel) : this()

    override fun load(limit: Int, onSuccess: (List<HomeElement>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getTrendingMemes(skip, limit).call({ memes ->
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                onSuccess(withContext(Dispatchers.Default) {
                    val homeElements = mutableListOf<HomeElement>()
                    memes.forEachIndexed { index, meme ->
                        homeElements.add(meme)
                        if (((skip + index + 1) - adOffset) % adPeriod == 0L) {
                            homeElements.add(AdElement(MemeItApp.instance))
                        }
                    }
                    homeElements
                })
            }
        }, onError)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrendingMemeLoader> {
        override fun createFromParcel(parcel: Parcel): TrendingMemeLoader {
            return TrendingMemeLoader(parcel)
        }

        override fun newArray(size: Int): Array<TrendingMemeLoader?> {
            return arrayOfNulls(size)
        }
    }
}