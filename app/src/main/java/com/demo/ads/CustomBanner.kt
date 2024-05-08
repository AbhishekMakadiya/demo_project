package com.demo.ads

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.demo.App

import com.google.android.gms.ads.*
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.demo.R


class CustomBanner : FrameLayout {
    private var mActivity: Activity
    private var bannerType: String? = null
    private var bannerIdKey: String? = null
    private var nativeIdKey: String? = null

    constructor(context: Context) : super(context) {
        mActivity = context as Activity
    }

    constructor(context: Context, atr: AttributeSet?) : super(context, atr) {
        mActivity = context as Activity
    }

    fun loadBanner(
        activity: Activity,
        banner_type_key: String?,
        banner_id_key: String?,
        native_id_key: String?
    ) {
        mActivity = activity
        bannerType = banner_type_key
        this.bannerIdKey = banner_id_key
        this.nativeIdKey = native_id_key
        initView()
    }

    fun loadBanner(activity: Activity, banner_type_key: String?, banner_id_key: String?) {
        mActivity = activity
        bannerType = banner_type_key
        this.bannerIdKey = banner_id_key
        initView()
    }

    fun loadNative(activity: Activity, banner_type_key: String?, native_id_key: String?) {
        mActivity = activity
        bannerType = banner_type_key
        this.nativeIdKey = native_id_key
        initView()
    }

    fun loadBanner(activity: Activity, banner_id_key: String?) {
        mActivity = activity
        this.bannerIdKey = banner_id_key
        loadBannerAd()
    }

    fun loadNative(activity: Activity, native_id_key: String?) {
        mActivity = activity
        this.nativeIdKey = native_id_key
        loadNativeAd(ONLY_NATIVE)
    }

    private fun getString(key: String): String {
        var bannerTypeValue = App.instance!!.getRemoteString(key) + ""

        val preferences = mActivity.getSharedPreferences("custom_ads", Context.MODE_PRIVATE)
        if (bannerTypeValue.isEmpty()) {
            bannerTypeValue = preferences.getString(key, "").toString()
//            AdUtility.Log("$key Custom banner from preference value $bannerTypeValue")
        } else {
            preferences.edit().putString(key, bannerTypeValue).apply()
        }
        Log.i("$key banner value $bannerTypeValue")
        return bannerTypeValue
    }

    private val isGlobalAdOff: Boolean
        get() {
            val adOnOff = getString(AdUtility.REMOTE_AD_ON_OFF) + ""
//            AdUtility.Log("Custom Banner Remove ad global value $adOnOff")
            return adOnOff.trim { it <= ' ' }.isEmpty() || adOnOff == "0"
        }

    private fun initView() {

        if (!Log.showAds)
            return
        if (isGlobalAdOff) return
        val bannerTypeValue = getString(bannerType!!)


        val adViewFrame = getString(AdUtility.REMOTE_SHOW_BANNER_BORDER) + ""
        if (bannerTypeValue != "0" && adViewFrame.isNotEmpty() && adViewFrame != "0") {
            try {
                val adViewFrameSize = getString(AdUtility.REMOTE_SHOW_BANNER_DP_SIZE) + ""
                var size = 0
                if (adViewFrameSize.isNotEmpty()) {
                    size = adViewFrameSize.toInt()
                    val dp2 = AdUtility.dp2px(mActivity, size.toFloat())
                    setPadding(dp2, dp2, dp2, dp2)
                } else {
                    val dp2 = AdUtility.dp2px(mActivity, 2f)
                    setPadding(dp2, dp2, dp2, dp2)
                }

                val params = layoutParams
                params.width = LayoutParams.MATCH_PARENT
                if (bannerTypeValue == ONLY_NATIVE) {
                    params.height = AdUtility.dp2px(mActivity, 286f + size + size)
                } else {
                    params.height =
                        AdUtility.dp2px(mActivity, adSize.height.toFloat() + size + size)
                }
                val textView = TextView(mActivity)
                textView.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                val loadText = getString(AdUtility.REMOTE_SHOW_BANNER_LOAD_TEXT) + ""
                textView.text = loadText
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                addView(textView)
                layoutParams = params
                val adViewBgColor = getString(AdUtility.REMOTE_SHOW_BANNER_BORDER_COLOR) + ""
                if (adViewBgColor.isNotEmpty()) {
                    setBackgroundColor(Color.parseColor(adViewBgColor))
                } else {
                    setBackgroundColor(Color.WHITE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        when (bannerTypeValue) {
            BANNER_NATIVE_OFF -> {}
            ONLY_BANNER -> loadBannerAd()
            ONLY_NATIVE, NATIVE_FIRST_SECOND_BANNER, NATIVE_RELOAD -> loadNativeAd(bannerTypeValue)
        }
    }

    private fun loadBannerAd() {
        if (isGlobalAdOff) return
        try {
            val bannerIdValue = getString(bannerIdKey!!)
            val adView = AdView(mActivity)
            // Step 4 - Set the adaptive ad size on the ad view.
            adView.setAdSize(adSize)
            if (bannerIdValue.isNotEmpty()) {
                adView.adUnitId = bannerIdValue
            }
            removeView(adView)
            addView(adView)
            if (Log.showAds)
                adView.loadAd(AdUtility().adRequest)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Step 2 - Determine the screen width (less decorations) to use for the ad width.
    private val adSize: AdSize
        get() {
            if (adWidth == 0) {
                // Step 2 - Determine the screen width (less decorations) to use for the ad width.
                val display = mActivity.windowManager.defaultDisplay
                val outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                val widthPixels = outMetrics.widthPixels.toFloat()
                val density = outMetrics.density
                adWidth = (widthPixels / density).toInt() - AdUtility.dp2px(mActivity, 2f)
            }
            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth)
        }

    private fun requestNativeAd(frameLayout: FrameLayout?, native_id_value: String) {

        if (nativeAd == null && !isLoading) {
            isLoading = true
            val builder = AdLoader.Builder(mActivity, native_id_value)
                .forNativeAd { nativeAds: NativeAd? ->
                    nativeAd = nativeAds
                    frameLayout?.let {
                        getNativeAdView(it)
                        isLoading = false
                    }
                }
            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isLoading = false
                    if (bannerType != null && bannerType!!.isNotEmpty()) {
                        val bannerTypeValue = getString(bannerType!!)
                        if (bannerTypeValue == NATIVE_FIRST_SECOND_BANNER) {
                            loadBannerAd()
                        }
                    }
                }

                override fun onAdImpression() {
                    super.onAdImpression()
//                AdUtility.Log("Native ad onAdImpression")
//                requestNativeAd(null, native_id_value)
                    nativeAd = null
                    isLoading = false
                }

            }).build()
            if (Log.showAds)
                adLoader.loadAd(AdUtility().adRequest)
        } else {
            getNativeAdView(this)
        }
    }

    private fun loadNativeAd(banner_type_value: String?) {
        if (isGlobalAdOff) return
        val nativeIdValue = getString(nativeIdKey!!)
        val frameLayout = FrameLayout(mActivity)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        frameLayout.layoutParams = params
        removeAllViews()
        addView(frameLayout)
        if (nativeAd == null || banner_type_value == NATIVE_RELOAD) {
            requestNativeAd(frameLayout, nativeIdValue)
        } else {
            getNativeAdView(frameLayout)
        }
    }


    private fun getNativeAdView(frameLayout: FrameLayout) {
        val nativeAdView = mActivity.layoutInflater
            .inflate(R.layout.native_adapter_layout, null) as NativeAdView
        displayNativeAd(nativeAdView, nativeAd)
        frameLayout.removeAllViews()
        frameLayout.addView(nativeAdView)
    }

    private fun displayNativeAd(adView: NativeAdView, nativeAd: NativeAd?) {
        try {
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            val mediaView: MediaView = adView.findViewById(R.id.ad_media)
            adView.mediaView = mediaView
            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd!!.headline
            adView.mediaView!!.setMediaContent(
                nativeAd.mediaContent!!
            )

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView!!.visibility = INVISIBLE
            } else {
                adView.bodyView!!.visibility = VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView!!.visibility = INVISIBLE
            } else {
                adView.callToActionView!!.visibility = VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView!!.visibility = GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                adView.iconView!!.visibility = VISIBLE
            }
            val vc = nativeAd.mediaContent!!.videoController
            if (vc.hasVideoContent()) {
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                }
            }
            adView.setNativeAd(nativeAd)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val BANNER_NATIVE_OFF = "0"
        const val ONLY_BANNER = "1"
        const val ONLY_NATIVE = "2"
        const val NATIVE_FIRST_SECOND_BANNER = "3"
        const val NATIVE_RELOAD = "4"
        private var adWidth = 0

        @JvmStatic
        private var nativeAd: NativeAd? = null
        private var isLoading = false
    }
}