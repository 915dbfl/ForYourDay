package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DDayData(
    @SerializedName("ID") val id: Int?,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("main") val main: Boolean,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("ddate") val ddate: Long,
    @SerializedName("content") val content: String
):Serializable
