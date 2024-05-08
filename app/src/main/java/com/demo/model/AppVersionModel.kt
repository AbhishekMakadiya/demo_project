package com.demo.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppVersionModel(
    @SerializedName("android_app_version")
    var androidAppVersion: String = "",
    @SerializedName("web_site_link")
    var webSiteLink: String = "",
    @SerializedName("android_app_features")
    var androidAppFeatures: String = ""
): Serializable