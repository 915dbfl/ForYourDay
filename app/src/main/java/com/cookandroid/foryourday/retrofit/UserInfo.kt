package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(
    @SerializedName("userId") var userId: Int?,
    @SerializedName("email") var email: String?,
    @SerializedName("userName") var userName: String?,
    @SerializedName("imagePath") var imagePath: String?
): Serializable
