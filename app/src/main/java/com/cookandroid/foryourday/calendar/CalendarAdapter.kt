package com.cookandroid.foryourday.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.cookandroid.foryourday.R
import java.util.*

class CalendarAdapter(context: Context, days: ArrayList<Date>, inputMonth: Int): ArrayAdapter<Date>(context, R.layout.custom_calendar, days){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val inputMonth = inputMonth

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val calendar = Calendar.getInstance()

        val date = getItem(position)

        calendar.time = date
        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONDAY)
        val year = calendar.get(Calendar.YEAR)

        val today = Date()
        val calToday = Calendar.getInstance()
        calToday.time = today

        if(view == null){
            view = inflater.inflate(R.layout.calendar_day, parent, false)
        }

        (view as TextView).setTypeface(null, Typeface.NORMAL)
        view.setTextColor(Color.BLACK)

        if(month != inputMonth || year != calToday.get(Calendar.YEAR)){
            view.setTextColor(Color.parseColor("#C4C4C4"))
        }
        else if(day == calToday.get(Calendar.DATE)
            && month == calToday.get(Calendar.MONTH)
            && year == calToday.get(Calendar.YEAR)){
            view.setTextColor(Color.parseColor("#56a6a9"))
        }

        view.text = calendar.get(Calendar.DATE).toString()

        return view
    }
}