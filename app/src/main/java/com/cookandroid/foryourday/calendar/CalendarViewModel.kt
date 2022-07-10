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

    private val _pickerDate = MutableLiveData<Date>().apply {
        val calInstance = Calendar.getInstance()
        value = calInstance.time
    }

    private var _calAdapter = MutableLiveData<CalendarAdapter>()
    private var _weeklyAdapter = MutableLiveData<CalendarAdapter>()

    private var _calendar = MutableLiveData<CalendarView>()
    private var _weeklyCalendar = MutableLiveData<WeeklyCalendarView>()

    private val _calPosition = MutableLiveData<Int>()
    private val _weeklyPosition = MutableLiveData<Int?>()

    val pickerDate: LiveData<Date> = _pickerDate

    val date: LiveData<Date> = _date

    val calAdapter: LiveData<CalendarAdapter> = _calAdapter
    val weeklyAdapter: LiveData<CalendarAdapter> = _weeklyAdapter

    var calendar: LiveData<CalendarView> = _calendar
    private var weeklyCalendar: LiveData<WeeklyCalendarView> = _weeklyCalendar

    val calPosition: LiveData<Int> = _calPosition
    val weeklyPosition: LiveData<Int?> = _weeklyPosition

    fun updateDate(date: Date){
        _date.value = date
    }

    fun updateCalendar(){
        val calDataPosition = calAdapter.value!!.getPosition(date.value!!)
        val weeklyDataPosition = weeklyAdapter.value?.getPosition(date.value!!)

        calAdapter.value!!.apply {
            notifyItemChanged(calPosition.value!!)
            if(calDataPosition != -1){
                pos = calDataPosition
                notifyItemChanged(calDataPosition)
                setCalPosition(calDataPosition)
            }else{
                val calInstance = Calendar.getInstance()
                calInstance.time = date.value!!
                calendar.value!!.updateCalendar(calInstance, date.value!!)
            }
        }

        weeklyAdapter.value?.apply {
            notifyItemChanged(weeklyPosition.value!!)
            if(weeklyDataPosition != -1){
                pos = weeklyDataPosition!!
                notifyItemChanged(weeklyDataPosition)
                setWeeklyPosition(weeklyDataPosition)
            }else{
                val calInstance = Calendar.getInstance()
                calInstance.time = date.value!!
                weeklyCalendar.value?.updateCalendar(calInstance, date.value!!)
            }
        }
    }

    fun updatePickerDate(date: Date){
        _pickerDate.value = date
    }

    fun setCalAdapter(adapter: CalendarAdapter){
        _calAdapter.value = adapter
    }

    fun setWeeklyAdapter(adapter: CalendarAdapter){
        _weeklyAdapter.value = adapter
    }

    fun setCalPosition(pos: Int){
        _calPosition.value = pos
    }

    fun setWeeklyPosition(pos: Int?){
        _weeklyPosition.value = pos
    }

    fun setCalendarView(view: CalendarView){
        _calendar.value = view
    }

    fun setWeeklyCalendarView(view: WeeklyCalendarView){
        _weeklyCalendar.value = view
    }
}