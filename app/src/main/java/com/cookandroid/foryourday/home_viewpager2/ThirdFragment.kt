package com.cookandroid.foryourday.home_viewpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.d_day.DDayCategoryRecyclerViewAdapter
import com.cookandroid.foryourday.d_day.DDayViewModel
import com.cookandroid.foryourday.databinding.ViewpagerThirdFragmentBinding
import com.cookandroid.foryourday.retrofit.DDayData
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThirdFragment: Fragment() {
    private var _binding: ViewpagerThirdFragmentBinding? = null
    private val dDayViewModel: DDayViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var dDayRecyclerView: RecyclerView
    private lateinit var addDDayMessage: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ViewpagerThirdFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dDayRecyclerView = binding.dDayRecyclerview
        addDDayMessage = binding.addDDayMessage
        val btnAddDDay = binding.btnAddDDay

        val sqlite = SQLite(context!!)
        CoroutineScope(Dispatchers.Main).launch {
            val dDays = sqlite.getDDays().await()
            if((dDays[1] as ArrayList<*>).size == 0){
                addDDayMessage.visibility = View.VISIBLE
            }else{
                val adapter = DDayCategoryRecyclerViewAdapter(
                    (dDays[0] as ArrayList<ArrayList<DDayData>>),
                    (dDays[1] as List<String>),
                    (dDays[2] as List<String>),
                    context!!
                )
                dDayRecyclerView.layoutManager = LinearLayoutManager(context)
                dDayRecyclerView.adapter = adapter
            }
        }

        dDayViewModel.updatePosition.observe(this, {
            CoroutineScope(Dispatchers.Main).launch {
                val dDays = sqlite.getDDays().await()
                if((dDays[1] as ArrayList<*>).size == 0){
                    addDDayMessage.visibility = View.VISIBLE
                }else{
                    addDDayMessage.visibility = View.GONE
                }
                val adapter = DDayCategoryRecyclerViewAdapter(
                    (dDays[0] as ArrayList<ArrayList<DDayData>>),
                    (dDays[1] as List<String>),
                    (dDays[2] as List<String>),
                    context!!
                )
                dDayRecyclerView.layoutManager = LinearLayoutManager(context)
                dDayRecyclerView.adapter = adapter
            }
        })

        btnAddDDay.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_home, inclusive = false, saveState = true)
                .build()
            it.findNavController().navigate(R.id.nav_add_d_day, null, navOptions)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}