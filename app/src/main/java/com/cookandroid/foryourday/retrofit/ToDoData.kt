package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ToDoData(
    @SerializedName("ID") val id: Int?,
    @SerializedName("complete") var complete: Boolean,
    @SerializedName("content") val content: String,
    @SerializedName("date") val date: Long,
    @SerializedName("categoryId") val categoryId: Int
): Serializable
