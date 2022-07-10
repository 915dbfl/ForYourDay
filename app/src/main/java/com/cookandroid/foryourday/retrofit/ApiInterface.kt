package com.cookandroid.foryourday.retrofit

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {
    @PATCH("v1/todo/{id}")
    fun patchTodo(
        @Header("Authorization") value: String,
        @Path("id") id: Int,
        @Body todoData: ToDoData):Call<Void>

    @GET("v1/todos")
    fun getToDos(
        @Header("Authorization") value: String): Call<ToDos>

    @POST("v1/todo")
    fun addTodo(
        @Header("Authorization") value: String,
        @Body todoData: ToDoData): Call<ToDo>

    @GET("v1/categories")
    fun getCategories(
        @Header("Authorization") value: String): Call<Categories>

    @POST("v1/category")
    fun addCategory(
        @Header("Authorization") value: String,
        @Body categoryData: CategoryData): Call<Category>

    @GET("v1/ddays")
    fun getDDays(
        @Header("Authorization") value: String): Call<DDays>

    @POST("v1/dday")
    fun addDDay(
        @Header("Authorization") value: String,
        @Body dDayData : DDayData): Call<DDay>

    @PATCH("v1/dday/{id}")
    fun patchDDay(
        @Header("Authorization") value: String,
        @Path("id") id: Int,
        @Body dDayData: DDayData):Call<Void>

    @POST("v1/oauth/login")
    fun checkUser(@Body userData: UserInfo): Call<UserData>

    @POST("v1/user")
    fun addUser(@Body userData: UserInfo): Call<UserData>

    @DELETE("v1/category/{id}")
    fun deleteCategory(
        @Header("Authorization") value: String,
        @Path("id") id: Int):Call<Void>

    @DELETE("v1/todo/{id}")
    fun deleteTodo(
        @Header("Authorization") value: String,
        @Path("id") id: Int):Call<Void>

    @DELETE("v1/dday/{id}")
    fun deleteDDay(
        @Header("Authorization") value: String,
        @Path("id") id: Int):Call<Void>

    @PATCH("v1/user/{id}")
    fun patchUser(
        @Header("Authorization") value: String,
        @Path("id") id: Int,
        @Body userData: UserInfo): Call<Void>

    @DELETE("v1/user/{id}")
    fun deleteUser(
        @Header("Authorization") value: String,
        @Path("id") id: Int
    ): Call<Void>

    companion object{
        private var BASE_URL = "https://www.todo.youlhyuk.com"

        fun create(): ApiInterface{
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}