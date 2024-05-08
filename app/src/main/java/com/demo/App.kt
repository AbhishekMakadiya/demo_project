package com.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.demo.ads.AdUtility
import com.demo.ads.AppOpenManager
import com.demo.ads.AppOpenManagerSplash
import com.demo.ui.splash.SplashActivity
import com.google.android.gms.ads.MobileAds

import com.demo.utils.LocaleManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: MultiDexApplication() {

    private var appOpenManagerSplash: AppOpenManagerSplash? = null
    private var appOpenManager: AppOpenManager? = null
//    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
//    private var firebaseAnalytics: FirebaseAnalytics? = null;



//    @Synchronized
//    open fun getInstance(): App? {
//        if (mInstance == null) mInstance =
//            App()
//        return mInstance
//    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
//        firebaseAnalytics = Firebase.analytics

    }

    companion object {
        @JvmStatic
        var instance: App? = null

        @kotlin.jvm.JvmField
        var clickCount: Int = 0

    }

    fun initAppOpenSplash(activity: Activity?) {
        if (appOpenManagerSplash == null)
            appOpenManagerSplash = AppOpenManagerSplash(instance!!, activity!!)
    }

    fun initAppOpen() {
        appOpenManager = AppOpenManager(instance!!)
    }

    fun getRemoteString(activity: Activity, key: String): String {
        var value = ""
        if (AdUtility.isRemoteConfigRunning) {
            return ""
        }
        if (AdUtility.isRemoteConfigFailed) {
//            reFetchRemoteConfig()
            return "";
        }
//        if (mFirebaseRemoteConfig != null)
//            value = mFirebaseRemoteConfig!!.getString(key);
        return ""
    }

    fun getRemoteString(key: String): String {
        var value = ""
        if (AdUtility.isRemoteConfigRunning) {
            return "";
        }
//        if (mFirebaseRemoteConfig != null)
//            value = instance!!.mFirebaseRemoteConfig!!.getString(key);
        return value
    }

//    fun reFetchRemoteConfig(activity: SplashActivity) {
//        FirebaseApp.initializeApp(instance!!)
//        instance!!.mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
//        val configSettings = FirebaseRemoteConfigSettings.Builder()
//            .setMinimumFetchIntervalInSeconds(2)
//            .build()
//        instance!!.mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
//        AdUtility.isRemoteConfigRunning = true
//        instance!!.mFirebaseRemoteConfig!!.fetchAndActivate().addOnCompleteListener {
//            if (it.isSuccessful) {
//                AdUtility.IS_REMOTE_UPDATED = true
//                AdUtility.isRemoteConfigFailed = false
//
//            } else {
//                AdUtility.IS_REMOTE_UPDATED = false
//                AdUtility.isRemoteConfigFailed = true
//            }
//            AdUtility.isRemoteConfigRunning = false
//            activity.fetchComplete()
////            Toast.makeText(
////                instance!!,
////                "Remote Config Fetched " + it.isSuccessful,
////                Toast.LENGTH_LONG
////            ).show()
//        }
//
//    }

//    fun loadHomeScreen(activity: Activity) {
//
//        val mainIntent = Intent(this, MainActivity::class.java)
//        activity.startActivity(mainIntent)
//        activity.finish()
//
//    }


}