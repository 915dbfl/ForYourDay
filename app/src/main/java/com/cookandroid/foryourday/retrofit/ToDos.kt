package com.cookandroid.foryourday.retrofit

import com.google.gson.annotations.SerializedName

data class ToDos(
    @SerializedName("data") val todos: List<ToDoData>
)