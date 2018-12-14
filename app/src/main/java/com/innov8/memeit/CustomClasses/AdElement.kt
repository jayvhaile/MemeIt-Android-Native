package com.innov8.memeit.CustomClasses

import android.content.Context
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdListener
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.Utils.measure
import com.memeit.backend.models.HomeElement

class AdElement(context: Context) : HomeElement {
    override val itemType: Int = HomeElement.AD_TYPE
    private var adLoaded = false
    private var listenerSet=false
    val nativeAd: NativeAd by lazy {
        measure { NativeAd(context, MemeItApp.FACEBOOK_AD_PLACEMENT_ID ) }
    }

    fun loadAd() {
        if (!adLoaded) nativeAd.loadAd()
        adLoaded = true
    }

    fun setAdListener(nativeAdListener: NativeAdListener) {
        if(!listenerSet)nativeAd.setAdListener(nativeAdListener)
        listenerSet=true
    }
}