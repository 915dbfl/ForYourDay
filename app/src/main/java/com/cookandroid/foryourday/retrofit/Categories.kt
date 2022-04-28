package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("data") val categories: List<ColorData>
)
