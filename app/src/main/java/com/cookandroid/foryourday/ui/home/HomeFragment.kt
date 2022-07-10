package com.cookandroid.foryourday.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.home_viewpager2.FirstFragment
import com.cookandroid.foryourday.home_viewpager2.SecondFragment
import com.cookandroid.foryourday.home_viewpager2.ThirdFragment
import com.cookandroid.foryourday.home_viewpager2.ViewPager2Adapter


class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewpager2Adapter = ViewPager2Adapter(this)

        viewpager2Adapter.addFragment(FirstFragment())
        viewpager2Adapter.addFragment(SecondFragment())
        viewpager2Adapter.addFragment(ThirdFragment())
        view.findViewById<ViewPager2>(R.id.viewpager2).adapter = viewpager2Adapter
    }
}