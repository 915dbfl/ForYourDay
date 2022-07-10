package com.cookandroid.foryourday.home_viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.databinding.ViewpagerSecondFragmentBinding
import java.util.*

class SecondFragment: Fragment() {
    private var _binding: ViewpagerSecondFragmentBinding? = null
    private val binding get() = _binding!!
    private val calendarViewModel: CalendarViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ViewpagerSecondFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calInstance = Calendar.getInstance()
        calInstance.time = calendarViewModel.date.value!!
        val weeklyCalendar = binding.weeklyCalendarView
        calendarViewModel.setWeeklyCalendarView(weeklyCalendar)
        weeklyCalendar.setCalendarViewModel(calendarViewModel)
        weeklyCalendar.updateCalendar(calInstance, calInstance.time)

        val todoRecycler = binding.todoRecycler
        todoRecycler.setCalendarViewModel(calendarViewModel)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}