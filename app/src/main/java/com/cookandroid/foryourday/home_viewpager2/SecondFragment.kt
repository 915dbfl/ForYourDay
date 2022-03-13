package com.cookandroid.foryourday.home_viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.WeeklyCalendarView
import java.util.*

class SecondFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.viewpager_second_fragment, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weeklyCalendar = view.findViewById<WeeklyCalendarView>(R.id.weekly_calendar_view)
        val calInstance = Calendar.getInstance()
        weeklyCalendar.updateCalendar(calInstance)
    }
}