package com.demo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryModel(
    @SerializedName("name")
    var name: String = "" // batsman
) : Serializable