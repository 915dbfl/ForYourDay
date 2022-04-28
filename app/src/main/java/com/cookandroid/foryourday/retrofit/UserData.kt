package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserData(
    @SerializedName("auth") val oauth: OAuthInfo,
    @SerializedName("user") val user: UserInfo
): Serializable
