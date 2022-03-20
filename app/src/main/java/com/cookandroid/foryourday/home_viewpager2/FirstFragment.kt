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
import com.cookandroid.foryourday.databinding.ViewpagerFirstFragmentBinding
import java.util.*

class FirstFragment: Fragment {
    private var dataset: ArrayList<ArrayList<String>>
    private var _binding: ViewpagerFirstFragmentBinding? = null

    private val binding get() = _binding!!

    constructor(dataSet: ArrayList<ArrayList<String>>){
        dataset = dataSet
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ViewpagerFirstFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendar = binding.customCalendar
        val calInstance = Calendar.getInstance()
        calendar.updateCalendar(calInstance)

        val dDayRecyclerView = binding.dDayRecyclerview
        val dDayAdapter = DdayRecyclerAdapter(dataset)
        dDayRecyclerView.layoutManager = LinearLayoutManager(this.context)
        dDayRecyclerView.adapter = dDayAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}