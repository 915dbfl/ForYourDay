package com.cookandroid.foryourday.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import java.util.*

class WeeklyCalendarView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private lateinit var calendarView: RecyclerView
    private lateinit var calAdapter: CalendarAdapter
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var textCMonth: TextView
    private lateinit var btnBWeek: ImageButton
    private lateinit var btnNWeek: ImageButton
    private lateinit var chosenD: Date
    private lateinit var curDate: Date
    private var months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    init {
        initControl(context!!)
    }

    fun updateCalendar(inputCalendar: Calendar, chosenDate: Date){
        chosenD = chosenDate
        curDate = inputCalendar.time
        textCMonth.text = months[inputCalendar.get(Calendar.MONTH)]
        val cells = ArrayList<Date>()
        val sep = 1 - inputCalendar.get(Calendar.DAY_OF_WEEK)
        val month = inputCalendar.get(Calendar.MONTH)
        val curDay = inputCalendar.get(Calendar.DAY_OF_MONTH)

        inputCalendar.set(Calendar.DAY_OF_MONTH, curDay + sep)
        inputCalendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        while(cells.size < 7){
            cells.add(inputCalendar.time)
            inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calAdapter = CalendarAdapter(
            context, cells, month, calendarViewModel, -1
        )
        calendarViewModel.setWeeklyAdapter(calAdapter)
        calendarView.adapter = calAdapter
    }

    private fun initControl(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_calendar, this)
        calendarView = findViewById(R.id.calendar_days)
        textCMonth = findViewById(R.id.text_cmonth)
        btnBWeek = findViewById(R.id.btn_bmonth)
        btnNWeek = findViewById(R.id.btn_nmonth)

        btnBWeek.setOnClickListener {
            gotoPastWeek()
        }

        btnNWeek.setOnClickListener {
            gotoNextWeek()
        }
    }

    fun setCalendarViewModel(model: CalendarViewModel){
        calendarViewModel = model
    }

    private fun gotoPastWeek(){
        val calInstance = Calendar.getInstance()
        calInstance.time = curDate
        calInstance.add(Calendar.DAY_OF_MONTH, -7)
        updateCalendar(calInstance, chosenD)
    }

    private fun gotoNextWeek(){
        val calInstance = Calendar.getInstance()
        calInstance.time = curDate
        calInstance.add(Calendar.DAY_OF_MONTH, 7)
        updateCalendar(calInstance, chosenD)
    }
}