package com.demo.utils


import android.util.Log
import com.demo.BuildConfig


object LogHelper {

    var showLogs = BuildConfig.DEBUG

    fun e(tag: String, message: String) {
        if (showLogs)
            Log.e(tag, message)
    }

    fun e(tag: String, message: String, e: Exception) {
        if (showLogs)
            Log.e(tag, message, e)
    }

    fun e(tag: String, e: Exception?) {
        if (showLogs) {
            if (e?.message != null)
                Log.e(tag, e.message!!)
        }
    }

    fun d(tag: String, message: String) {
        if (showLogs)
            Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        if (showLogs)
            Log.i(tag, message)
    }

    fun v(tag: String, message: String) {
        if (showLogs)
            Log.v(tag, message)
    }

    fun printStackTrace(e: Exception) {
        if (showLogs)
            e.printStackTrace()
    }
    fun println(msg: Any) {
        if (showLogs)
            kotlin.io.println(msg)
    }
}
