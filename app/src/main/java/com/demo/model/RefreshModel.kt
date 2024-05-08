package com.demo.model


import com.google.gson.annotations.SerializedName

data class RefreshModel(
    @SerializedName("token")
    var token: String
)