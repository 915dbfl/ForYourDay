package com.cookandroid.foryourday.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridView
import android.widget.LinearLayout
import com.cookandroid.foryourday.R
import java.util.*

class WeeklyCalendarView: LinearLayout {
    lateinit var gridView: GridView

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs){
        initControl(context!!)
    }

    fun updateCalendar(inputCalendar: Calendar){
        val cells = ArrayList<Date>()
        val sep = 1 - inputCalendar.get(Calendar.DAY_OF_WEEK)
        val month = inputCalendar.get(Calendar.MONTH)
        val curDay = inputCalendar.get(Calendar.DAY_OF_MONTH)
        inputCalendar.set(Calendar.DAY_OF_MONTH, curDay + sep)

        while(cells.size < 7){
            cells.add(inputCalendar.time)
            inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        gridView.adapter = CalendarAdapter(
            context, cells, month
        )
    }

    private fun initControl(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_weekly_calendar, this)
        gridView = findViewById(R.id.calendar_days)
    }
}