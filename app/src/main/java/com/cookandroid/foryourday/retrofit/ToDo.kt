package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class ToDo(
    @SerializedName("data") val todo: ToDoData
)
