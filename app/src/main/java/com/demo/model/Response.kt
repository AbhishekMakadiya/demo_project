package com.demo.model

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("success")
    val isSuccess: Boolean = false,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("code")
    var code: Int = 0,
    @SerializedName("result")
    var result: T? = null
)
