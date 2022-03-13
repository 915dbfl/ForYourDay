package com.cookandroid.foryourday.home_viewpager2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    val fragList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragList[position]
    }

    fun addFragment(fragment: Fragment){
        fragList.add(fragment)
        notifyItemInserted(fragList.size-1)
    }

}