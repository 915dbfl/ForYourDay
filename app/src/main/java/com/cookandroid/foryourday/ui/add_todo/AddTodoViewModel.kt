package com.cookandroid.foryourday.ui.add_todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTodoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        val calInstance = Calendar.getInstance()
        val year = calInstance.get(Calendar.YEAR)
        val month = calInstance.get(Calendar.MONTH)+1
        val date = calInstance.get(Calendar.DATE)
        value = "$year.$month.$date"
    }
    val text: LiveData<String> = _text

    fun updateToDoDate(date: String) {
        _text.value = date
    }

    
}