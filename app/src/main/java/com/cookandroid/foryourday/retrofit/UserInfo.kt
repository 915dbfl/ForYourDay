package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import java.io.Serializable

data class UserInfo(
    @SerializedName("userId") var userId: Int?,
    @SerializedName("email") var email: String?,
    @SerializedName("userName") var userName: String?
): Serializable
