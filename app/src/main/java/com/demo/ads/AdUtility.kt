package com.demo.ads

import android.content.Context
import android.net.ConnectivityManager
import android.util.TypedValue
import com.demo.App
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.*

class AdUtility {
    val adRequest: AdRequest
        get() {
            if (isTestAd) {
                val testDeviceIds = listOf(
                    "1B7A03160D114A6511833C3232214E19",
                    "05DD700CF606B5553625D8F0D6CAF3C4",
                    "F07455BF6E907DD885E4A7D9E818DD91",
                    "8D7EEFEB739B93F5F32DA9D10BA63E6B",
                    "B8997BF33C2D76606C7D4EC87CD81968",
                    "90C86ABBDDDC6A8B7B7399CA3A6FACF0",
                    "7DE6164199B1967F2E8198398801DFED",
                    "8C5BB8FEFBDA9B69D33B500BBC009FA8",
                    "22A1E1353A109B2480ACF79276F45AAE",
                    "4BFC3A01FA943D50C8E1DD31C973B749"
                )
                val configuration =
                    RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
                MobileAds.setRequestConfiguration(configuration)
            }
            return AdRequest.Builder().build()
        }

    companion object {
        private const val TAG = "CUSTADS"

        var isTestAd = true

        @JvmField
        var isRemoteConfigRunning = false

        @JvmField
        var isRemoteConfigFailed = false

        @JvmField
        var IS_REMOTE_UPDATED = true

        const val REMOTE_SHOW_BANNER_BORDER = "remote_show_banner_border"
        const val REMOTE_SHOW_BANNER_BORDER_COLOR = "remote_show_banner_border_color"
        const val REMOTE_SHOW_BANNER_DP_SIZE = "remote_show_banner_dp_size"
        const val REMOTE_SHOW_BANNER_LOAD_TEXT = "remote_show_banner_load_text"

        const val REMOTE_AD_ON_OFF = "remote_ad_on_off" // full app ad on off
        const val INTERSTITIAL_CLICK_INTERVAL = "interstitial_click_interval"




        const val REMOTE_SPLASH_INTER_AD_ON_OFF =
            "remote_splash_inter_ad_on_off" //0-off 1-banner 2-native
        const val REMOTE_SPLASH_INTER_ID = "remote_splash_inter_id"

        const val REMOTE_APP_OPEN_AD_ON_OFF = "remote_app_open_ad_on_off"
        const val REMOTE_APP_OPEN_ID = "remote_app_open_id"
        const val REMOTE_SPLASH_APP_OPEN_AD_ON_OFF = "remote_splash_app_open_ad_on_off"
        const val REMOTE_SPLASH_APP_OPEN_ID = "remote_splash_app_open_id"

        const val REMOTE_MAIN_ACTIVITY_BANNER_BOTTOM_TYPE = "remote_main_activity_banner_bottom_type"
        const val REMOTE_MAIN_ACTIVITY_INTER_AD_ON_OFF = "remote_main_activity_inter_ad_on_off"


        const val REMOTE_SETTING_ACTIVITY_BANNER_BOTTOM_TYPE =
            "remote_setting_activity_banner_bottom_type"
        const val REMOTE_SETTING_ACTIVITY_INTER_AD_ON_OFF =
            "remote_setting_activity_inter_ad_on_off"

        const val REMOTE_GESTURE_ACTIVITY_BANNER_BOTTOM_TYPE = "remote_gesture_activity_banner_bottom_type"
        const val REMOTE_GESTURE_ACTIVITY_INTER_AD_ON_OFF = "remote_gesture_activity_inter_ad_on_off"

        const val REMOTE_DISPLAY_ACTIVITY_BANNER_BOTTOM_TYPE = "remote_display_activity_banner_bottom_type"
        const val REMOTE_DISPLAY_ACTIVITY_INTER_AD_ON_OFF = "remote_display_activity_inter_ad_on_off"


        const val REMOTE_BANNER_ID = "remote_banner_id"
        const val REMOTE_NATIVE_ID = "remote_native_id"
        const val REMOTE_INTER_ID = "remote_inter_id"
        const val REMOTE_EXIT_BANNER_TYPE = "remote_exit_banner_type"

        // Firebase App Dialog
        const val REMOTE_APP_DIALOG_ON_OFF = "remote_app_dialog_on_off"
        const val REMOTE_APP_DIALOG_TITLE = "remote_app_dialog_title"
        const val REMOTE_APP_DIALOG_DESCRIPTION = "remote_app_dialog_description"
        const val REMOTE_APP_LINK = "remote_app_link"


        fun setTestModeAd(testMode: Boolean) {
            isTestAd = testMode
        }

        @JvmStatic
        var screen_sequence: ArrayList<String> = ArrayList()

        @JvmStatic
        var click_interstitial_interval = 1

        @JvmStatic
        fun checkConnectivity(lCon: Context?): Boolean {
            return isNetworkConnected(lCon)
        }

        private fun isNetworkConnected(lCon: Context?): Boolean {
            if (lCon != null) {
                val cm = lCon.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                return Objects.requireNonNull(cm).activeNetworkInfo != null
            }
            return false
        }

        fun dp2px(context: Context, dp: Float): Int {
            val r = context.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.displayMetrics
            )
            return px.toInt()
        }

        @JvmStatic
        fun Log(message: String) {
            Log.i(TAG, message + "")
        }

        fun getClickInterval(): Boolean {
            val value = App.instance!!.getRemoteString(INTERSTITIAL_CLICK_INTERVAL)
            var interval = 0
            try {
                if (value.isNotEmpty()) interval = value.toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            Log("click interval $interval click count " + App.clickCount)
            if (interval == 0)
                return false
            return App.clickCount >= interval
        }
    }

}