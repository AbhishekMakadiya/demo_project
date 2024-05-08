package com.demo.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BannerImage(
    @SerializedName("image_path")
    var imagePath: String
): Serializable