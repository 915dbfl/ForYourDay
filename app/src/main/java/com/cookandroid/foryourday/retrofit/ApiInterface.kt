package com.cookandroid.foryourday.retrofit

import android.graphics.Color
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @GET("v1/categories")
    fun getCategories(
        @Header("Authorization") value: String
    ): Call<Categories>

    @POST("v1/category")
    fun addCategory(
        @Header("Authorization") value: String,
        @Body colorData: ColorData): Call<Void>

    @POST("v1/oauth/login")
    fun checkUser(@Body userData: UserInfo): Call<UserData>

    @POST("v1/user")
    fun addUser(@Body userData: UserInfo): Call<UserData>

    companion object{
        var BASE_URL = "https://www.todo.youlhyuk.com"

        fun create(): ApiInterface{
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}