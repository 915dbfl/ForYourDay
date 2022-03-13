package com.cookandroid.foryourday.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.databinding.FragmentHomeBinding
import com.cookandroid.foryourday.home_viewpager2.FirstFragment
import com.cookandroid.foryourday.home_viewpager2.SecondFragment
import com.cookandroid.foryourday.home_viewpager2.ThirdFragment
import com.cookandroid.foryourday.home_viewpager2.ViewPager2Adapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewpager2Adapter = ViewPager2Adapter(this)

        val base = ArrayList<ArrayList<String>>()
        val data2 = ArrayList<String>()
        data2.add("7")
        data2.add("새해")
        data2.add("루틴")
        base.add(data2)
        val data3 = ArrayList<String>()
        data3.add("1")
        data3.add("크리스마스")
        data3.add("루틴")
        base.add(data3)

        viewpager2Adapter.addFragment(FirstFragment(base))
        viewpager2Adapter.addFragment(SecondFragment())
        viewpager2Adapter.addFragment(ThirdFragment())
        view.findViewById<ViewPager2>(R.id.viewpager2).adapter = viewpager2Adapter
    }
}