package com.cookandroid.foryourday.retrofit

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgApiInterface {

    @POST("/image")
    @Multipart
    fun postImage(
        @Part file: MultipartBody.Part?): Call<ImageData>

    companion object{
        private var BASE_URL = "https://www.image.todo.youlhyuk.com"

        fun create(): ImgApiInterface{
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ImgApiInterface::class.java)
        }
    }
}