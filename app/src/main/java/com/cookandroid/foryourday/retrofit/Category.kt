package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("data") val category: CategoryData
)
