package com.cookandroid.foryourday.home_viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.foryourday.databinding.ViewpagerSecondFragmentBinding
import java.util.*

class SecondFragment: Fragment() {
    private var _binding: ViewpagerSecondFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ViewpagerSecondFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val weeklyCalendar = binding.weeklyCalendarView
        val calInstance = Calendar.getInstance()
        weeklyCalendar.updateCalendar(calInstance)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}