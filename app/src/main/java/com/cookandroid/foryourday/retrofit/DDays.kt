package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class DDays(
    @SerializedName("data") val dDays: List<DDayData>
)
