package com.cookandroid.foryourday.home_viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.d_day.MainDDayRecyclerViewAdapter
import com.cookandroid.foryourday.calendar.CalendarView
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.d_day.DDayViewModel
import com.cookandroid.foryourday.databinding.ViewpagerFirstFragmentBinding
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FirstFragment: Fragment() {
    private var _binding: ViewpagerFirstFragmentBinding? = null
    private val binding get() = _binding!!
    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val dDayViewModel: DDayViewModel by activityViewModels()
    private lateinit var calendar: CalendarView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ViewpagerFirstFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        calendar = binding.customCalendar
        calendarViewModel.setCalendarView(calendar)
        val calInstance = Calendar.getInstance()
        calendar.setCalendarViewModel(calendarViewModel)
        calendar.updateCalendar(calInstance, calInstance.time)

        val dDayRecyclerView = binding.dDayRecyclerview
        val sqlite = SQLite(context!!)
        CoroutineScope(Dispatchers.Main).launch {
            val dDayDatas = sqlite.getMainDDays().await()
            val dDayAdapter = MainDDayRecyclerViewAdapter(dDayDatas, context!!)
            dDayRecyclerView.layoutManager = LinearLayoutManager(context)
            dDayRecyclerView.adapter = dDayAdapter
        }

        dDayViewModel.updatePosition.observe(this,{
            CoroutineScope(Dispatchers.Main).launch {
                val dDayDatas = sqlite.getMainDDays().await()
                val dDayAdapter = MainDDayRecyclerViewAdapter(dDayDatas, context!!)
                dDayRecyclerView.layoutManager = LinearLayoutManager(context)
                dDayRecyclerView.adapter = dDayAdapter
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}