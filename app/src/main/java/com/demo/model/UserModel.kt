package com.demo.model


import com.google.gson.annotations.SerializedName

data class UserModel(
        @SerializedName("id")
        var id: String = "",
        @SerializedName("fullname")
        var fullname: String = "",
        @SerializedName("user_name")
        var userName: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("mobile_no")
        var mobileNo: String = "",
        @SerializedName("country_code")
        var countryCode: String = "",
        @SerializedName("prefered_exchange")
        var preferedExchange: String = "",
        @SerializedName("wallet_amount")
        var walletAmount: String = "",
        @SerializedName("app_version")
        var app_version: AppVersionModel? = null
)