package com.cookandroid.foryourday.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.cookandroid.foryourday.R
import java.util.*
import kotlin.collections.ArrayList

class CalendarView : LinearLayout {
    lateinit var gridView: GridView
    lateinit var textCmonth: TextView
    lateinit var btn_bmonth: ImageButton
    lateinit var btn_nmonth: ImageButton
    lateinit var layout_header: LinearLayout
    lateinit var calAdapter: CalendarAdapter
    var months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    var month: Int = 0
    var year: Int = 0

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initControl(context!!)
    }

    fun updateCalendar(inputCalendar: Calendar){
        val cells = ArrayList<Date>()
        month = inputCalendar.get(Calendar.MONTH)
        year = inputCalendar.get(Calendar.YEAR)
        textCmonth.text = months[month]

        inputCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthBeginningCell = inputCalendar.get(Calendar.DAY_OF_WEEK) -1

        inputCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while(cells.size < 42){
            cells.add(inputCalendar.time)
            inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calAdapter = CalendarAdapter(
            context, cells, month
        )
        gridView.adapter = calAdapter

    }

    private fun initControl(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_calendar, this)
        gridView = findViewById(R.id.calendar_days)
        textCmonth = findViewById(R.id.text_cmonth)
        btn_bmonth = findViewById(R.id.btn_bmonth)
        btn_nmonth = findViewById(R.id.btn_nmonth)
        layout_header = findViewById(R.id.calendar_header)

        btn_bmonth.setOnClickListener {
            gotoPastMonth()
        }

        btn_nmonth.setOnClickListener {
            gotoNextMonth()
        }
    }

    private fun gotoPastMonth(){
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.YEAR, year)
        calInstance.set(Calendar.MONTH, month)
        calInstance.add(Calendar.MONTH, -1)
        updateCalendar(calInstance)
    }

    private fun gotoNextMonth(){
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.YEAR, year)
        calInstance.set(Calendar.MONTH, month)
        calInstance.add(Calendar.MONTH, 1)
        updateCalendar(calInstance)
    }

}