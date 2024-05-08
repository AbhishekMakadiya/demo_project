package com.demo.ads

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.demo.App
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class AppOpenManager(private val myApplication: App) : LifecycleObserver,
    ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var currentActivity: Activity? = null


    private fun getString(key: String): String? {
        val preferences = myApplication.getSharedPreferences("custom_ads", Context.MODE_PRIVATE)
        var bannerTypeValue = preferences.getString(key, "")
        Log.i("$key appOpen from pref  value $bannerTypeValue")
        if (bannerTypeValue == "" || AdUtility.IS_REMOTE_UPDATED) {
            bannerTypeValue = App.instance!!.getRemoteString(key)
            Log.i("$key appOpen from firebase  value $bannerTypeValue")
            preferences.edit().putString(key, bannerTypeValue).apply()
        }
        return bannerTypeValue
    }

    fun fetchAd() {
        val adOnOff = getString(AdUtility.REMOTE_AD_ON_OFF)
        if (adOnOff == "0") return
        // Have unused ad, no need to fetch another.
        val adOnOffValue = getString(AdUtility.REMOTE_APP_OPEN_AD_ON_OFF)
        if (adOnOffValue == "0") return
        val appOpenId = getString(AdUtility.REMOTE_APP_OPEN_ID)
        if (isAdAvailable) {
            return
        }
        if (isLoading)
            return
        isLoading = true
        loadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isLoading = false
                Log.i("onAppOpenAdLoaded : " + ad.responseInfo)
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
                isLoading = false
                Log.i("onAppOpenAdFailedToLoad : " + loadAdError.message)
            }
        }
        val request = AdUtility().adRequest
        AppOpenAd.load(
            myApplication, appOpenId!!, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback!!
        )
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    private val isAdAvailable: Boolean
        get() = appOpenAd != null

    /**
     * ActivityLifecycleCallback methods
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    private fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (isReadyPlaceToAppOpenAds && !isShowingAd && isAdAvailable) {
            isShowingAd = true
            Log.i("Will show ad.")
            Log.i("Suitable Place For Display AppOpen Ads")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(currentActivity!!)
        } else {
            fetchAd()
        }
    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    fun onStart() {
        Log.i("onStart AppOpen Call")
        showAdIfAvailable()
    }

    companion object {
        @kotlin.jvm.JvmField
        var isReadyPlaceToAppOpenAds: Boolean = true

        private var isShowingAd = false

        @kotlin.jvm.JvmStatic
        private var isLoading: Boolean = false
    }


    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        fetchAd()
    }
}