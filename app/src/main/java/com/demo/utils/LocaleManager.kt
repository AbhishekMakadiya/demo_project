package com.demo.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringDef
import java.util.*

object LocaleManager {
    const val ENGLISH = "en"
    const val GUJARATI = "gu"
    const val HINDI = "hi"

    /**
     * set current pref locale
     */
    fun setLocale(mContext: Context): Context {
        return updateResources(mContext, PreferenceManager(mContext).getLanguagePref())
    }

    /**
     * Set new Locale with context
     */
    fun setNewLocale(
        mContext: Context,
        @LocaleDef language: String
    ): Context {
        PreferenceManager(mContext).setLanguagePref(language)
        return updateResources(mContext, language)
    }


    /**
     * update resource
     */
    private fun updateResources(
        context: Context,
        language: String?
    ): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config =
            Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    /**
     * get current locale
     */
    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales[0] else config.locale
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @StringDef(ENGLISH, HINDI, GUJARATI)
    annotation class LocaleDef {
        companion object {
            var SUPPORTED_LOCALES = arrayOf(ENGLISH, HINDI, GUJARATI)
        }
    }
}