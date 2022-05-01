package com.cookandroid.foryourday.ui.add_todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.databinding.FragmentAddTodoBinding
import com.cookandroid.foryourday.main.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTodoFragment :androidx.fragment.app.Fragment() {

    private lateinit var addTodoViewModel: AddTodoViewModel
    private lateinit var calendarViewModel: CalendarViewModel
    private var _binding: FragmentAddTodoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTodoViewModel = ViewModelProvider(this).get(AddTodoViewModel::class.java)
        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        val cal = binding.addTodoCal
        val todoDate = binding.textviewTodoDate
        val btnComplete = binding.btnAddTodoComplete
        val btnCancel = binding.btnAddTodoCancel
        val categoryRecycler = binding.categoryRecyclerview
        val textViewAddCategory = binding.textviewAddCategory

        val calInstance = Calendar.getInstance()
        cal.updateCalendar(calInstance)

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val categories = mainViewModel.categories.value

        val categoryRecyclerViewAdapter = AddToDoRecyclerViewAdapter(mainViewModel.categories.value!!, context!!)
        categoryRecycler.layoutManager = LinearLayoutManager(context)
        categoryRecycler.adapter = categoryRecyclerViewAdapter

        if(categories!!.isEmpty()){
            textViewAddCategory.visibility = View.VISIBLE
        }

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

        textViewAddCategory.setOnClickListener {
            it.findNavController().navigate(R.id.nav_add_category)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}