package com.cookandroid.foryourday.ui.add_todo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.databinding.FragmentAddTodoBinding

class AddTodoFragment : Fragment() {

    private lateinit var addTodoViewModel: AddTodoViewModel
    private var _binding: FragmentAddTodoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        addTodoViewModel =
            ViewModelProvider(this).get(AddTodoViewModel::class.java)

        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val picker: DatePicker = binding.todoPicker
        val todoDate: TextView = binding.textviewTodoDate
        val btnComplete: Button = binding.btnAddTodoComplete
        val btnCancel: Button = binding.btnAddTodoCancel

        picker.setOnDateChangedListener { _, i, i2, i3 ->
            addTodoViewModel.updateToDoDate("$i."+(i2+1)+".$i3")
        }

        btnComplete.setOnClickListener {
            //데이터베이스에 값 추가!
            it.findNavController().navigate(R.id.nav_home)
            //토스트 메세지 띄우기!
        }

        btnCancel.setOnClickListener {
            it.findNavController().navigate(R.id.nav_home)
        }


        addTodoViewModel.text.observe(viewLifecycleOwner, Observer {
            todoDate.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}