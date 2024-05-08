package com.demo.ads

import android.app.Activity
import android.content.Context
import com.demo.App
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class CustomInterstitialAds {
    private val INTER_OFF = "0"
    private val INTER_BACKPRESS_ONLY = "2"
    private val INTER_BOTH_FORWARD_BACKPRESS = "3"
    val INTER_FORWARD_ONLY by lazy { "1" }
    private val INTER_REWARD_FORWARD_ONLY = "4"
    private val mActivity: Activity
    private var listener: CustomInterstitialCallback? = null
    private var backPressListener: CustomInterstitialBackPressCallback? = null

    private var clickBackPress = false
    private var adOnOff1: String = ""
    private var interstitialIdKey: String = ""

    constructor(mActivity: Activity) {
        this.mActivity = mActivity
    }

    constructor(mActivity: Activity, ad_on_off: String, inter_id: String) {
        this.mActivity = mActivity
        this.adOnOff1 = ad_on_off
        interstitialIdKey = inter_id
        loadInterstitial(ad_on_off, inter_id)
    }

    fun setCustomInterstitialListener(listener: CustomInterstitialCallback?) {
        this.listener = listener
    }

    fun setCustomInterstitialBackListener(listener: CustomInterstitialBackPressCallback) {
        backPressListener = listener
    }

    private fun getString(key: String): String {
        var interstitialTypeValue = App.instance!!.getRemoteString(key)
        Log.i("$key key from firebase value $interstitialTypeValue")
        val preferences = mActivity.getSharedPreferences("custom_ads", Context.MODE_PRIVATE)
        if (interstitialTypeValue == null || interstitialTypeValue.isEmpty()) {
            interstitialTypeValue = preferences.getString(key, "").toString()
            Log.i("$key key from pref value $interstitialTypeValue")
        } else {
            preferences.edit().putString(key, interstitialTypeValue).apply()
        }
        return interstitialTypeValue
    }

    private val isGlobalAdOff: Boolean
        get() {
            val adOnOff = getString(AdUtility.REMOTE_AD_ON_OFF) + ""
//            AdUtility.Log("Remote ad global value $adOnOff")
            return adOnOff.trim { it <= ' ' }.isEmpty() || adOnOff == "0"
        }

    fun loadInterstitial(ad_on_off: String, inter_id: String) {
        this.adOnOff1 = ad_on_off
        if (isGlobalAdOff) return

        val adOnOffValue = getString(ad_on_off)
        if (adOnOffValue == INTER_OFF) {
            return
        }
        val clickIntervalStr = getString(AdUtility.INTERSTITIAL_CLICK_INTERVAL)

        if (clickIntervalStr.isNotEmpty()) {
            val clickInterval = clickIntervalStr.toInt()
            val nextClickCounter = clickCounter
            if (nextClickCounter % clickInterval != 0)
                return
        }
        val interIdValue = getString(inter_id) + ""
        if (interIdValue.isNotEmpty())
            requestInterstitial(interIdValue, false)
    }

    fun loadSplashInterstitial(ad_on_off: String, inter_id: String) {
        this.adOnOff1 = ad_on_off
        if (isGlobalAdOff) {
            if (listener != null) listener!!.onAdFailedToShow()
            return
        }

        val adOnOffValue = getString(ad_on_off)
        if (adOnOffValue == INTER_OFF) {
            if (listener != null) listener!!.onAdFailedToShow()
            return
        }
        val interValueId = getString(inter_id)
        if (mInterstitialAd != null) {
            showInterstitial()
        }
        requestInterstitial(interValueId, true)
    }

    private fun requestInterstitial(inter_id_value: String, isSplash: Boolean) {
        if (mInterstitialAd == null && !isLoading) {
            isLoading  = true
            val adRequest = AdUtility().adRequest
            InterstitialAd.load(mActivity, inter_id_value, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        isLoading  = false
                        Log.i("onAdLoaded $adOnOff1")
                        if (isSplash && listener != null) listener!!.onAdLoaded()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.i(loadAdError.message)
                        mInterstitialAd = null
                        isLoading  = false
                        if (isSplash && listener != null) listener!!.onAdFailedToShow()
                    }
                })
        }else{
            if (isSplash && listener != null) listener!!.onAdLoaded()
        }
    }

    fun showInterstitial() {
        clickCounter++
        clickBackPress = false
        val adOnOffValue = getString(adOnOff1)
        if (adOnOffValue == INTER_OFF) {
            if (listener != null) {
                listener!!.onAdFailedToShow()
            }
            return
        }
        if (adOnOffValue == INTER_FORWARD_ONLY || adOnOffValue == INTER_REWARD_FORWARD_ONLY || adOnOffValue == INTER_BOTH_FORWARD_BACKPRESS) {
            if (mInterstitialAd != null) {
                interstitialListener(false)
                mInterstitialAd!!.show(mActivity)
            } else if (listener != null) {
                listener!!.onAdFailedToShow()
                Log.i("The interstitial ad wasn't ready yet.")
            }
        } else {
            if (listener != null) listener!!.onAdFailedToShow()
            Log.i("The interstitial not show. tag not match ")
        }
    }

    fun showSplashInterstitial() {
        clickBackPress = false
        val adOnOffValue = getString(adOnOff1)
//                && clickCounter%AdUtility.click_interstitial_interval!=0
        if (adOnOffValue == INTER_OFF) {
            if (listener != null) {
                listener!!.onAdFailedToShow()
            }
            return
        }
        if (adOnOffValue == INTER_FORWARD_ONLY || adOnOffValue == INTER_REWARD_FORWARD_ONLY || adOnOffValue == INTER_BOTH_FORWARD_BACKPRESS) {
            if (mInterstitialAd != null) {
                interstitialListener(true)
                mInterstitialAd!!.show(mActivity)
            } else if (listener != null) {
                listener!!.onAdFailedToShow()
                Log.i("The interstitial ad wasn't ready yet.")
            }
        } else {
            if (listener != null) listener!!.onAdFailedToShow()
            Log.i("The interstitial not show. tag not match ")
        }
    }

    fun showAndLoadNextInterstitial(ad_on_off: String, inter_id: String) {
        clickBackPress = false
        val adOnOffValue = getString(this.adOnOff1)
        Log.i("showAndLoadNextInterstitial : $adOnOffValue")
        if (adOnOffValue == INTER_OFF) {
            if (listener != null) {
                listener!!.onAdFailedToShow()
            }
            return
        }
        this.adOnOff1 = ad_on_off
        interstitialIdKey = inter_id
        if (adOnOffValue == INTER_FORWARD_ONLY || adOnOffValue == INTER_REWARD_FORWARD_ONLY || adOnOffValue == INTER_BOTH_FORWARD_BACKPRESS) {
            if (mInterstitialAd != null) {
                interstitialListener(false)
                mInterstitialAd!!.show(mActivity)
            } else {
                if (listener != null) listener!!.onAdFailedToShow()
                Log.i("The interstitial ad wasn't ready yet.")
            }
        } else {
            if (listener != null) listener!!.onAdFailedToShow()
            Log.i("The interstitial not show. tag not match ")
        }
    }

    private fun interstitialListener(isSplash: Boolean) {
        mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.i("The ad was dismissed.")
                App.clickCount = 0
                if (!clickBackPress && listener != null) listener!!.onAdDismissed() else if (clickBackPress && backPressListener != null) backPressListener!!.onComplete()
                if (!isSplash) {
                    loadInterstitial(adOnOff1, interstitialIdKey)
    //                    String inter_value_id = getString(interstitial_id_key);
    //                    requestInterstitial(inter_value_id, isSplash);
                }

            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                Log.i("The ad failed to show.")
                if (!clickBackPress && listener != null) listener!!.onAdFailedToShow() else if (clickBackPress && backPressListener != null) backPressListener!!.onComplete()
            }

            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
                Log.i("The ad was shown.$adOnOff1")
            }
        }
    }

    fun showBackInterstitial() {
        Log.i("showBackInterstitial call")
        val adOnOffValue = getString(adOnOff1)
        if (isGlobalAdOff) {
            if (backPressListener != null) backPressListener!!.onComplete()
            return
        }
        //2 - only back ad show
//3 - Show both forward and back ads
        if (adOnOffValue == INTER_BACKPRESS_ONLY || adOnOffValue == INTER_BOTH_FORWARD_BACKPRESS) {
            Log.i("showBackInterstitial call if")
            clickBackPress = true
            if (mInterstitialAd != null) {
                Log.i("showBackInterstitial call mInterstitialAd not null")
                mInterstitialAd!!.show(mActivity)
                interstitialListener(false)
            } else {
                Log.i("showBackInterstitial call mInterstitialAd null")
                if (backPressListener != null) backPressListener!!.onComplete()
            }
        } else {
            Log.i("showBackInterstitial call else")
            if (backPressListener != null) {
                App.clickCount = 0
                Log.i("showBackInterstitial call backlistener not null")
                backPressListener!!.onComplete()
            }
        }
    }

    interface CustomInterstitialCallback {
        fun onAdDismissed()
        fun onAdFailedToLoad()
        fun onAdFailedToShow()
        fun onAdLoaded()
    }

    interface CustomInterstitialBackPressCallback {
        fun onComplete()
    }

    companion object {
        private var mInterstitialAd: InterstitialAd? = null

        @JvmStatic
        var clickCounter = 0

        private var isLoading  = false
    }
}