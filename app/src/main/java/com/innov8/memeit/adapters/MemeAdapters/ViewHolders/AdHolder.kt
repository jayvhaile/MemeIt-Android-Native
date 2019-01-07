package com.innov8.memeit.adapters.MemeAdapters.ViewHolders

import android.util.Log
import android.view.View
import android.widget.*
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.facebook.ads.*
import com.github.ybq.android.spinkit.SpinKitView
import com.innov8.memeit.adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.utils.AdElement
import com.innov8.memeit.R
import com.innov8.memeit.commons.toast
import com.memeit.backend.models.HomeElement

class AdHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val loading: SpinKitView = itemView.findViewById(R.id.ad_holder_loading)
    private lateinit var adView: View
    private lateinit var nativeAdIcon: AdIconView
    private lateinit var nativeAdTitle: TextView
    private lateinit var nativeAdMedia: MediaView
    private lateinit var nativeAdSocialContext: TextView
    private lateinit var nativeAdBody: TextView
    private lateinit var sponsoredLabel: TextView
    private lateinit var nativeAdCallToAction: Button
    private lateinit var adChoicesContainer: LinearLayout
    private val listener: NativeAdListener


    private var viewInflated = false
    private var bindWaiting = false


    init {
        itemView as FrameLayout

        AsyncLayoutInflater(memeAdapter.context).inflate(R.layout.list_item_ad, itemView) { view, _, _ ->
            viewInflated = true
            adView = view
            adView.visibility = View.GONE
            initAdViews()
            itemView.addView(adView)
            if (bindWaiting) {
                bindAd((memeAdapter.items[itemPosition] as AdElement).nativeAd)
                bindWaiting = false
            }
        }
        listener = object : NativeAdListener {
            override fun onAdClicked(p0: Ad) {
                Log.w("Ad","Clicked")

            }

            override fun onMediaDownloaded(p0: Ad) {
                Log.w("Ad","Media downloaded")
            }

            override fun onError(p0: Ad, p1: AdError) {
                memeAdapter.context.toast("ad error ${p1.errorCode}\n ${p1.errorMessage}",Toast.LENGTH_LONG)

            }

            override fun onLoggingImpression(p0: Ad) {
                Log.w("Ad","logging impression")
            }

            override fun onAdLoaded(ad: Ad) {
                Log.w("Ad","Loaded")

                bindAd((memeAdapter.items[itemPosition] as AdElement).nativeAd)
            }
        }
    }

    private fun initAdViews() {
        nativeAdIcon = adView.findViewById(R.id.native_ad_icon)
        nativeAdTitle = adView.findViewById(R.id.native_ad_title)
        nativeAdMedia = adView.findViewById(R.id.native_ad_media)
        nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context)
        nativeAdBody = adView.findViewById(R.id.native_ad_body)
        sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label)
        nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action)
        adChoicesContainer = adView.findViewById(R.id.ad_choices_container)
    }

    private fun bindAd(nativeAd: NativeAd) {
        if (viewInflated) {
            loading.visibility = View.GONE
            nativeAd.unregisterView()
            adChoicesContainer.removeAllViews()
            val adChoicesView = AdChoicesView(memeAdapter.context, nativeAd, true)
            adChoicesContainer.addView(adChoicesView, 0)
            nativeAdTitle.text = nativeAd.advertiserName
            nativeAdBody.text = nativeAd.adBodyText
            nativeAdSocialContext.text = nativeAd.adSocialContext
            nativeAdCallToAction.visibility = if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
            nativeAdCallToAction.text = nativeAd.adCallToAction
            sponsoredLabel.text = nativeAd.sponsoredTranslation
            val clickableViews = listOf(nativeAdTitle, nativeAdCallToAction)
            nativeAd.registerViewForInteraction(
                    itemView,
                    nativeAdMedia,
                    nativeAdIcon,
                    clickableViews)
            adView.visibility = View.VISIBLE
        } else {
            bindWaiting = true
        }
    }


    override fun bind(homeElement: HomeElement) {
        val adElement = homeElement as AdElement
        loading.visibility = View.VISIBLE
        if (viewInflated) adView.visibility = View.GONE
        if (!adElement.nativeAd.isAdLoaded) {
            adElement.setAdListener(listener)
            adElement.loadAd()
        } else bindAd(adElement.nativeAd)
    }
}