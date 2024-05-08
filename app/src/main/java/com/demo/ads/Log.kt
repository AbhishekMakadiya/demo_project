package com.demo.ads

import com.demo.BuildConfig


object Log {
    var showAds = true
    var isDebugMode = BuildConfig.DEBUG

    // log some information
    fun i( message: String) {
        if (isDebugMode) {
            android.util.Log.i("CUSTADS", "" + message)
        }
    }

    fun i(tag: String, message: String) {
        if (isDebugMode) {
            android.util.Log.i(tag, "" + message)
        }
    }

    // log some error
    fun e(tag: String, message: String) {
        if (isDebugMode) {
            android.util.Log.e(tag, "" + message)
        }
    }

    // log some waring
    fun w(tag: String, message: String) {
        if (isDebugMode) {
            android.util.Log.w(tag, "" + message)
        }
    }

    // log verbose
    fun v(tag: String, message: String) {
        if (isDebugMode) {
            android.util.Log.v(tag, "" + message)
        }
    }

    // log some debug info
    fun d(tag: String, message: String) {
        if (isDebugMode) {
            android.util.Log.d(tag, "" + message)
        }
    }
}
