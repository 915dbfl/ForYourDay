package com.cookandroid.foryourday.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.Categories
import com.cookandroid.foryourday.retrofit.ColorData
import com.cookandroid.foryourday.retrofit.UserData
import retrofit2.Call
import retrofit2.Response

class MainViewModel() : ViewModel(){
    private val _data = MutableLiveData<UserData>()
    private val _categories = MutableLiveData<List<ColorData>?>()

    val data: LiveData<UserData> = _data
    var categories: LiveData<List<ColorData>?> = _categories

    fun setCategories(categories: List<ColorData>?){
        _categories.value = categories
    }

    fun setData(userData: UserData){
        _data.value = userData
    }

    fun updateCategories(){
        val accessToken = data.value!!.oauth.accessToken
        val header = "bare $accessToken"
        ApiInterface.create().getCategories(header).enqueue(
            object : retrofit2.Callback<Categories> {
                override fun onResponse(call: Call<Categories>, response: Response<Categories>) {
                    if(response.isSuccessful){
                        setCategories(response.body()!!.categories)
                    }
                }

                override fun onFailure(call: Call<Categories>, t: Throwable) {
                    Log.d("getUserCategoriesGetApi", "error: ${t.toString()}")
                }
            }
        )

    }
}