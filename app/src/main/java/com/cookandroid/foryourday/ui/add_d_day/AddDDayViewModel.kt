package com.cookandroid.foryourday.ui.add_d_day

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class AddDDayViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        val calInstance = Calendar.getInstance()
        val year = calInstance.get(Calendar.YEAR)
        val month = calInstance.get(Calendar.MONTH)+1
        val date = calInstance.get(Calendar.DATE)
        value = "$year.$month.$date"
    }
    val text: LiveData<String> = _text

    fun updateDDayDate(date: String) {
        _text.value = date
    }
}