package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("imagePath") val imagePath: String
)
