package com.cookandroid.foryourday.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import java.util.*
import kotlin.collections.ArrayList

class CalendarView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private lateinit var calendarView: RecyclerView
    private lateinit var textCMonth: TextView
    private lateinit var btnBMonth: ImageButton
    private lateinit var btnNMonth: ImageButton
    private lateinit var calAdapter: CalendarAdapter
    private var months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var chosenD: Date
    private lateinit var calendarViewModel: CalendarViewModel
    private var type = 0

    init {
        initControl(context!!)
    }

    fun updateCalendar(inputCalendar: Calendar, chosenDate: Date){
        val cells = ArrayList<Date>()
        chosenD = chosenDate
        month = inputCalendar.get(Calendar.MONTH)
        year = inputCalendar.get(Calendar.YEAR)
        textCMonth.text = months[month]

        inputCalendar.set(Calendar.DAY_OF_MONTH, 1)
        inputCalendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val monthBeginningCell = inputCalendar.get(Calendar.DAY_OF_WEEK) -1

        inputCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while(cells.size < 42){
            cells.add(inputCalendar.time)
            inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calAdapter = CalendarAdapter(
            context, cells, month, calendarViewModel, type
        )
        if(type == 0){
            calendarViewModel.setCalAdapter(calAdapter)
        }
        calendarView.adapter = calAdapter
    }

    private fun initControl(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_calendar, this)
        calendarView = findViewById(R.id.calendar_days)
        textCMonth = findViewById(R.id.text_cmonth)
        btnBMonth = findViewById(R.id.btn_bmonth)
        btnNMonth = findViewById(R.id.btn_nmonth)

        btnBMonth.setOnClickListener {
            gotoPastMonth()
        }

        btnNMonth.setOnClickListener {
            gotoNextMonth()
        }
    }

    private fun gotoPastMonth(){
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.YEAR, year)
        calInstance.set(Calendar.MONTH, month)
        calInstance.add(Calendar.MONTH, -1)
        updateCalendar(calInstance, chosenD)
    }

    private fun gotoNextMonth(){
        val calInstance = Calendar.getInstance()
        calInstance.set(Calendar.YEAR, year)
        calInstance.set(Calendar.MONTH, month)
        calInstance.add(Calendar.MONTH, 1)
        updateCalendar(calInstance, chosenD)
    }

    fun setCalendarViewModel(model: CalendarViewModel){
        calendarViewModel = model
    }

    fun setIsPicker(value: Int){
        type = value
    }
}