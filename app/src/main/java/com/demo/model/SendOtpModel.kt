package com.demo.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendOtpModel(
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("otp")
    var otp: String = ""
): Serializable