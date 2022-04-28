package com.cookandroid.foryourday.ui.add_todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarView
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.databinding.FragmentAddTodoBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddTodoFragment :androidx.fragment.app.Fragment() {

    private lateinit var addTodoViewModel: AddTodoViewModel
    private lateinit var calendarViewModel: CalendarViewModel
    private var _binding: FragmentAddTodoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTodoViewModel =
            ViewModelProvider(this).get(AddTodoViewModel::class.java)
        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        val cal: CalendarView = binding.addTodoCal
        val todoDate: TextView = binding.textviewTodoDate
        val btnComplete: Button = binding.btnAddTodoComplete
        val btnCancel: Button = binding.btnAddTodoCancel
        val categoryRecycler: RecyclerView = binding.categoryRecyclerview

        val calInstance = Calendar.getInstance()
        cal.updateCalendar(calInstance)

        val hash1 = HashMap<String, String>()
        hash1.put("중요", "#E29393")
        hash1.put("루틴", "#73A075")
        hash1.put("시험", "#FFBB86FC")

        val categoryRecyclerViewAdapter = AddToDoRecyclerViewAdapter(hash1, context!!)
        categoryRecycler.layoutManager = LinearLayoutManager(context)
        categoryRecycler.adapter = categoryRecyclerViewAdapter

        calendarViewModel.date.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val date = SimpleDateFormat("yyyy.M.dd").format(it)
            addTodoViewModel.updateToDoDate(date)
        })

        addTodoViewModel.text.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            todoDate.text = it
        })

        btnComplete.setOnClickListener {
            //데이터베이스에 값 추가!
            it.findNavController().navigate(R.id.nav_home)
        }

        btnCancel.setOnClickListener {
            it.findNavController().navigate(R.id.nav_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}