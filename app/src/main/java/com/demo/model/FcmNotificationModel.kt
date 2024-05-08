package com.demo.model


import com.google.gson.annotations.SerializedName

data class FcmNotificationModel(
    @SerializedName("body")
    var body: List<FcmNotificationBodyModel>
)