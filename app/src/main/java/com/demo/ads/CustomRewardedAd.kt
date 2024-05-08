package com.demo.ads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.demo.App
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.demo.R

class CustomRewardedAd {
    private var mRewardedAd: RewardedAd? = null
    private val mActivity: Activity
    private var listener: CustomRewardedCallback? = null
    private var progressDialog: Dialog? = null
    private var adOnOff1: String? = null
    private var rewardedIdKey: String? = null
    private var interstitialIdKey: String? = null
    var customInterstitialAds: CustomInterstitialAds? = null

    constructor(mActivity: Activity, ad_on_off: String?, rewarded_id: String?) {
        this.mActivity = mActivity
        this.adOnOff1 = ad_on_off
        rewardedIdKey = rewarded_id
        requestRewardedAd(false)
    }

    constructor(mActivity: Activity) {
        this.mActivity = mActivity
    }

    constructor(
        mActivity: Activity,
        ad_on_off: String?,
        rewarded_id_key: String?,
        inter_id_key: String?
    ) {
        this.mActivity = mActivity
        this.adOnOff1 = ad_on_off
        this.rewardedIdKey = rewarded_id_key
        interstitialIdKey = inter_id_key
        requestRewardedAd(false)
    }

    fun loadRewardedAd(ad_on_off: String?, rewarded_id_key: String?, inter_id_key: String?) {
        this.adOnOff1 = ad_on_off
        this.rewardedIdKey = rewarded_id_key
        interstitialIdKey = inter_id_key
        requestRewardedAd(false)
    }

    fun setCustomRewardedAdlListener(listener: CustomRewardedCallback?) {
        this.listener = listener
    }

    private fun getString(key: String): String {
        var interstitialTypeValue =  App.instance!!.getRemoteString(key) + ""
        Log.i("$key from firebase rewarded value $interstitialTypeValue")
        val preferences = mActivity.getSharedPreferences("custom_ads", Context.MODE_PRIVATE)
        if (interstitialTypeValue.isEmpty()) {
            interstitialTypeValue = preferences.getString(key, "").toString()
            Log.i("$key from pref rewarded value $interstitialTypeValue")
        } else {
            preferences.edit().putString(key, interstitialTypeValue).apply()
        }
        return interstitialTypeValue
    }

    private val isGlobalAdOff: Boolean
        get() {
            val adOnOff = getString(AdUtility.REMOTE_AD_ON_OFF) + ""
            Log.i("Remove ad global rewarded value $adOnOff")
            return adOnOff.trim { it <= ' ' }.isEmpty() || adOnOff == "0"
        }

    private fun requestRewardedAd(isRetry: Boolean) {
        if (!AdUtility.checkConnectivity(mActivity)) {
            return
        }
        if (isGlobalAdOff) {
            listener!!.onAdFailedToLoad()
            return
        }
        val adOnOffValue = getString(adOnOff1!!)
        if (adOnOffValue.isEmpty()) {
            listener!!.onAdFailedToLoad()
            return
        }
        when (adOnOffValue) {
            REWARDED_OFF -> return
            REWARDED_ONLY -> loadRewarded(isRetry)
            REWARDED_OR_INTERSTITIAL -> loadInterstitial()
        }
    }

    private fun loadInterstitial() {
        customInterstitialAds = CustomInterstitialAds(mActivity)
        customInterstitialAds!!.loadInterstitial(adOnOff1!!, interstitialIdKey!!)
        customInterstitialAds!!.setCustomInterstitialListener(object :
            CustomInterstitialAds.CustomInterstitialCallback {
            override fun onAdDismissed() {
                if (listener != null) listener!!.onAdDismissed()
            }

            override fun onAdFailedToLoad() {}
            override fun onAdFailedToShow() {
                if (listener != null) {
                    listener!!.onAdFailedToShow()
                }
            }
            override fun onAdLoaded() {}
        })
    }

    private fun loadRewarded(isRetry: Boolean) {
        if(!Log.showAds)
            return
        val rewardedIdValue = getString(rewardedIdKey!!)+""
        val adRequest = AdUtility().adRequest
        RewardedAd.load(mActivity, rewardedIdValue,
            adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error.
                    Log.i("Rewarded ad error msg " + loadAdError.message)
                    mRewardedAd = null
                    if (listener != null) listener!!.onAdFailedToLoad()
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    Log.i("Rewarded Ad was loaded.")
                    if (isRetry) {
                        if (progressDialog != null) progressDialog!!.dismiss()
                        initListener()
                        showRewardedAd()
                    } else {
                        listener!!.onAdLoaded()
                    }
                }
            })
    }

    private fun initListener() {
        mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.i("Rewarded Ad was shown. $adOnOff1")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.i("Rewarded Ad failed to show.")
                if (listener != null) listener!!.onAdFailedToShow()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.i("Rewarded Ad was dismissed.")
                mRewardedAd = null
            }
        }
    }

    fun showRewardedAd() {
        val adOnOffValue = getString(adOnOff1!!)
        if (isGlobalAdOff || adOnOffValue == REWARDED_OFF) {
            if (listener != null) {
                listener!!.onAdDismissed()
            }
        }
        when {
            mRewardedAd != null -> {
                initListener()
                mRewardedAd!!.show(mActivity) {
                    // Handle the reward.
                    Log.i("The user earned the reward.")
                    if (listener != null) listener!!.onAdDismissed()
                }
            }
            customInterstitialAds != null -> {
                customInterstitialAds!!.showInterstitial()
            }
            else -> {
                showLoadingDialog()
                requestRewardedAd(true)
            }
        }
    }

    interface CustomRewardedCallback {
        fun onAdDismissed()
        fun onAdFailedToLoad()
        fun onAdFailedToShow()
        fun onAdLoaded()
    }

    private fun showLoadingDialog() {
        progressDialog = ProgressDialog.progressDialog(mActivity)
        progressDialog!!.setCancelable(false)
//        progressDialog!!.setMessage("Loading Ad...")
//        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//        progressDialog!!.progress = 0
        progressDialog!!.show()
    }

    companion object {
        private const val REWARDED_OFF = "0"
        private const val REWARDED_ONLY = "1"
        private const val REWARDED_OR_INTERSTITIAL = "4"
    }
    class ProgressDialog {
        companion object {
            fun progressDialog(context: Activity): Dialog{
                val dialog = Dialog(context)
                val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
                dialog.setContentView(inflate)
                dialog.setCancelable(false)
                dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT)
                )
                return dialog
            }
        }
    }
}