package com.cookandroid.foryourday.ui.add_todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cookandroid.foryourday.databinding.FragmentAddTodoBinding

class AddTodoFragment : Fragment() {

    private lateinit var addTodoViewModel: AddTodoViewModel
    private var _binding: FragmentAddTodoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        addTodoViewModel =
            ViewModelProvider(this).get(AddTodoViewModel::class.java)

        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val picker: DatePicker = binding.todoPicker
        val todoDate: TextView = binding.textviewTodoDate
        val btnComplete: Button = binding.btnAddTodoComplete
        val btnCancel: Button = binding.btnAddTodoCancel

        addTodoViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}