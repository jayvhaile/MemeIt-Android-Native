package com.innov8.memeit.CustomClasses

import android.content.Context
import com.facebook.ads.NativeAd
import com.innov8.memeit.measure
import com.memeit.backend.dataclasses.HomeElement

class AdElement(context: Context) : HomeElement {
    override val itemType: Int = HomeElement.AD_TYPE
    val nativeAd: NativeAd by lazy {
        measure { NativeAd(context, "YOUR_PLACEMENT_ID") }
    }
}