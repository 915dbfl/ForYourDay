package com.cookandroid.foryourday.home_viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.DdayRecyclerAdapter
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarView
import java.util.*

class FirstFragment: Fragment {
    private var dataset: ArrayList<ArrayList<String>>

    constructor(dataSet: ArrayList<ArrayList<String>>){
        dataset = dataSet
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.viewpager_first_fragment, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = view.findViewById<CalendarView>(R.id.custom_calendar)
        val calInstance = Calendar.getInstance()
        calendar.updateCalendar(calInstance)

        val dDayRecyclerView = view.findViewById<RecyclerView>(R.id.d_day_recyclerview)
        val dDayAdapter = DdayRecyclerAdapter(dataset)
        dDayRecyclerView.layoutManager = LinearLayoutManager(this.context)
        dDayRecyclerView.adapter = dDayAdapter
    }
}