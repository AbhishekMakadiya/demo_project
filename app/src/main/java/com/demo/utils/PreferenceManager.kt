package com.demo.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.demo.model.UserModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context : Context) {

    private val preferences: SharedPreferences
    private val PRIVATE_MODE = 0

    //var context: Context

    init {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        //context = mContext
    }

    fun clearPrefrences() {
        preferences.edit().clear().apply()
    }

    fun removePreference(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun setStringPreference(key: String, stringValue: String) {
        preferences.edit().putString(key, stringValue).apply()
    }

    fun setBooleanPreference(key: String, booleanValue: Boolean) {
        preferences.edit().putBoolean(key, booleanValue).apply()
    }

    fun setIntPreference(key: String, intValue: Int) {
        preferences.edit().putInt(key, intValue).apply()
    }

    fun getStringPreference(key: String): String? {
        return preferences.getString(key, "")
    }

    fun getBooleanPreference(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    fun getIntPreference(key: String): Int {
        return preferences.getInt(key, -1)
    }

    /**
     * get user login or not
     */
    fun getUserLogin(): Boolean {
        return preferences.getBoolean(IS_USER_ALREADY_LOGIN, false)
    }
    /**
     * get user login or not
     */
    fun setUserLogin(isLogin:Boolean){
        preferences.edit().putBoolean(IS_USER_ALREADY_LOGIN, isLogin).apply()
    }

    /**
     * get user login or not
     */
    fun getUserType(): Int {
        return preferences.getInt(USER_TYPE, 0)
    }
    /**
     * get user login or not
     */
    fun setUserType(type:Int){
        preferences.edit().putInt(USER_TYPE, type).apply()
    }

    /**
     * get user login or not
     */
    fun getSubscribe(): Boolean {
        return preferences.getBoolean(IS_SUBSCRIBED, false)
    }
    /**
     * get user login or not
     */
    fun setSubscribe(isSubscribe:Boolean){
        preferences.edit().putBoolean(IS_SUBSCRIBED, isSubscribe).apply()
    }

    /**
     * get user login or not
     */
    fun getPreHome(): Boolean {
        return preferences.getBoolean(IS_PREMOHOME, false)
    }
    /**
     * get user login or not
     */
    fun setPreHome(isLogin:Boolean){
        preferences.edit().putBoolean(IS_PREMOHOME, isLogin).apply()
    }

    /**
     * set user data model
     */
    fun setUserData(userModel: UserModel) {
        setStringPreference(USER_LOGIN_DATA, Gson().toJson(userModel))
    }

    /**
     * get user data model
     */
    fun getUserData(): UserModel? {
        return Gson().fromJson(
            getStringPreference(USER_LOGIN_DATA),
            UserModel::class.java
        )
    }

    fun getUserId(): String? {
        return preferences.getString(APPUSERID, "")
    }

    fun setUserId(userId: String) {
        preferences.edit().putString(APPUSERID, userId).apply()
    }


    /**
     * Get saved Locale from SharedPreferences
     *
     * @param mContext current context
     * @return current locale key by default return english locale
     */
    fun getLanguagePref(): String? {
        return preferences.getString(LANGUAGE_KEY, LocaleManager.ENGLISH)
    }

    /**
     * set pref key
     */
    fun setLanguagePref(localeKey: String) {
        preferences.edit().putString(LANGUAGE_KEY, localeKey).apply()
    }


    /**
     * get remember me
     */
    fun getRememberMe(): Boolean {
        return preferences.getBoolean(IS_REMEMBER_ME, false)
    }
    /**
     * set remember me
     */
    fun setRememberMe(isLogin:Boolean){
        preferences.edit().putBoolean(IS_REMEMBER_ME, isLogin).apply()
    }

    /**
     * get user password
     */
    fun getUserPassword(): String? {
        return preferences.getString(USER_PASSWORD, "")
    }

    /**
     * set user password
     */
    fun setUserPassword(userId: String) {
        preferences.edit().putString(USER_PASSWORD, userId).apply()
    }


    /**
     * get WhatsApp Number
     */
    fun getWhatsAppNumber(): String? {
        return preferences.getString(WHATSAPP_NUMBER, "")
    }

    /**
     * set WhatsApp Number
     */
    fun setWhatsAppNumber(whatsAppNumber: String) {
        preferences.edit().putString(WHATSAPP_NUMBER, whatsAppNumber).apply()
    }

    companion object {
        const val PREF_NAME = "amcheritage"
        var FCM_TOKEN = "FCM_TOKEN"
        var TWILIO_TOKEN = "TWILIO_TOKEN"
        val VIDEO_BINDING = "VIDEO_BINDING"
        val NOTIFICATION_ID = "NOTIFICATION_ID"
        val IS_USER_ALREADY_LOGIN = "IS_USER_ALREADY_LOGIN"
        val USER_LOGIN_DATA = "USER_LOGIN_DATA"
        val LATITUDE = "LATITUDE"
        val LONGITUDE = "LONGITUDE"
        val APPUSERID = "APPUSERID"
        val IS_PREMOHOME = "IS_PREMOHOME"
        val SUBSCRIBE_DATE = "SUBSCRIBE_DATE"
        val IS_SUBSCRIBED = "IS_SUBSCRIBED"
        val IS_TAKING = "IS_TAKING"
        val USER_TYPE = "USER_TYPE"
        val LANGUAGE_KEY = "language_key"
        val IS_REMEMBER_ME= "IS_REMEMBER_ME"
        val USER_PASSWORD= "USER_PASSWORD"
        val WHATSAPP_NUMBER = "WHATSAPP_NUMBER"
    }
}
