package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OAuthInfo(
    @SerializedName("accessToken") var accessToken: String?,
    @SerializedName("refreshToken") var refreshToken: String?
): Serializable
