package com.cookandroid.foryourday.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CalendarViewModel: ViewModel() {
    private val _date = MutableLiveData<Date>().apply {
        val calInstance = Calendar.getInstance()
        value = calInstance.time
    }

    val date: LiveData<Date> = _date

    fun updateDate(date: Date){
        _date.value = date
    }
}