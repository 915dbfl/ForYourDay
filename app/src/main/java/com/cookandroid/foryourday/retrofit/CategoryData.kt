package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class CategoryData(
    @SerializedName("title") var title: String,
    @SerializedName("value") var value: String,
    @SerializedName("ID") var id: Int?
)
