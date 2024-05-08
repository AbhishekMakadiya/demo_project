package com.demo.model


import com.google.gson.annotations.SerializedName

data class FcmNotificationBodyModel(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("ref_id")
    var refId: Any,
    @SerializedName("tag")
    var tag: String,
    @SerializedName("user_id")
    var userId: String
)