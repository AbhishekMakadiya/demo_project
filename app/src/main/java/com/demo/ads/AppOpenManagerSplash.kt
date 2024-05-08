package com.demo.ads

import android.app.Activity
import android.content.Context
import com.demo.App
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback


class AppOpenManagerSplash(
    private var mApplication: App,
    private val activity: Activity
) {

    init {
        fetchAd()
    }

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null

    private fun getString(key: String): String? {
        val preferences = mApplication.getSharedPreferences("custom_ads", Context.MODE_PRIVATE)
        var bannerTypeValue = preferences.getString(key, "")
        Log.i("$key appOpen from pref  value $bannerTypeValue")
        if (bannerTypeValue == "" || AdUtility.IS_REMOTE_UPDATED) {
            bannerTypeValue = App.instance!!.getRemoteString(key)
            Log.i("$key appOpen from firebase  value $bannerTypeValue")
            preferences.edit().putString(key, bannerTypeValue).apply()
        }
        return bannerTypeValue
    }

    private fun fetchAd() {
        // Have unused ad, no need to fetch another.
        val adOnOffValue = getString(AdUtility.REMOTE_SPLASH_APP_OPEN_AD_ON_OFF)
        if (adOnOffValue == "0") return
        val appOpenId = getString(AdUtility.REMOTE_SPLASH_APP_OPEN_ID)

        loadCallback = object : AppOpenAdLoadCallback() {

            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                Log.i("onAppOpenAdLoaded : " + ad.responseInfo)
                showAdIfAvailable()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
                Log.i("onAppOpenAdFailedToLoad : " + loadAdError.message)
//                mApplication.loadHomeScreen(activity)
            }
        }
        val request = AdUtility().adRequest
        AppOpenAd.load(
            mApplication, appOpenId!!, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback!!
        )
    }


    private fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        Log.i("Will show ad.")
        Log.i("Suitable Place For Display AppOpen Ads")
        val fullScreenContentCallback: FullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
//                    mApplication.loadHomeScreen(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                    mApplication.loadHomeScreen(activity)
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
        appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
        appOpenAd!!.show(activity)

    }
}