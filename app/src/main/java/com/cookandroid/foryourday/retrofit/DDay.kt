package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class DDay(
    @SerializedName("data") val data: DDayData
)
