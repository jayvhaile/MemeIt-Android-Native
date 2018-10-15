package com.innov8.memeit.Adapters.MemeAdapters.ViewHolders

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.ads.*
import com.innov8.memeit.CustomClasses.AdElement
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter
import com.innov8.memeit.R
import com.innov8.memeit.log
import com.memeit.backend.dataclasses.HomeElement

class AdHolder(itemView: View, memeAdapter: MemeAdapter) : MemeViewHolder(itemView, memeAdapter) {
    private val nativeAdIcon: AdIconView = itemView.findViewById(R.id.native_ad_icon)
    private val nativeAdTitle: TextView = itemView.findViewById(R.id.native_ad_title)
    private val nativeAdMedia: MediaView = itemView.findViewById(R.id.native_ad_media)
    private val nativeAdSocialContext: TextView = itemView.findViewById(R.id.native_ad_social_context)
    private val nativeAdBody: TextView = itemView.findViewById(R.id.native_ad_body)
    private val sponsoredLabel: TextView = itemView.findViewById(R.id.native_ad_sponsored_label)
    private val nativeAdCallToAction: Button = itemView.findViewById(R.id.native_ad_call_to_action)
    private val adChoicesContainer: LinearLayout = itemView.findViewById(R.id.ad_choices_container)
    private val loading: ProgressBar = itemView.findViewById(R.id.ad_loading)
    val listener: NativeAdListener

    init {


        listener = object : NativeAdListener {
            override fun onAdClicked(p0: Ad) {
                log("qqq facebook add", "ad clicked")
            }

            override fun onMediaDownloaded(p0: Ad) {
                log("qqq facebook add", "media downloaded")

            }

            override fun onError(p0: Ad, p1: AdError) {
                log("qqq facebook add", "ad failed", p1.errorMessage)
            }

            override fun onLoggingImpression(p0: Ad) {
                log("qqq facebook add", "logging impression")
            }

            override fun onAdLoaded(ad: Ad) {
                log("qqq facebook add", "ad loaded")
                bindAd()
            }
        }
    }

    val nativeAd: NativeAd get() = (memeAdapter.items[itemPosition] as AdElement).nativeAd

    private fun bindAd() {
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
    }


    override fun bind(homeElement: HomeElement) {
        val adElement = homeElement as AdElement
        loading.visibility = View.VISIBLE
        if (!nativeAd.isAdLoaded) {
            nativeAd.setAdListener(listener)
            nativeAd.loadAd()
        } else bindAd()
    }
}