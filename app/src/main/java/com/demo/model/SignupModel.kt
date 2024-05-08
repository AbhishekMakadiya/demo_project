package com.demo.model


import com.google.gson.annotations.SerializedName

data class SignupModel(
    @SerializedName("signup")
    var signup: UserModel? = null,
    @SerializedName("suggestion")
    var suggestion: ArrayList<String> = ArrayList()
)