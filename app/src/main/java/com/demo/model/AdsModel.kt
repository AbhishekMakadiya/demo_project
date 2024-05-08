package com.demo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AdsModel(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("ad_banner_url")
    var adBannerUrl: String = "",
    @SerializedName("ad_link")
    var adLink: String = ""
) : Serializable