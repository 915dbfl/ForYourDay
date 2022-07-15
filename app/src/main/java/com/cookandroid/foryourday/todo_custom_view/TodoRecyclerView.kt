package com.cookandroid.foryourday.todo_custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.retrofit.ToDoData
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoRecyclerView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private lateinit var todoCategoryRecycler: RecyclerView
    private lateinit var todoCategoryRecyclerViewAdapter: TodoCategoryRecyclerViewAdapter
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var textTodoDate: TextView
    private val sqlite = SQLite(context!!)
    private lateinit var cnt: Context
    private lateinit var textAddTodo: TextView
    private var first = true

    init {
        init(context!!)
    }
    private fun init(context: Context){
        cnt = context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.todo_recyclerview, this)
        textAddTodo = findViewById(R.id.text_add_todo)
        todoCategoryRecycler = findViewById(R.id.todo_recycler_category)
        textTodoDate = findViewById(R.id.text_todo_date)

        textAddTodo.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_home, inclusive = false, saveState = true)
                .build()
            it.findNavController().navigate(R.id.nav_add_todo, null, navOptions)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(first){
            CoroutineScope(Dispatchers.Main).launch {
                val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val today = Calendar.getInstance().time
                textTodoDate.text = format.format(today)
                val result = sqlite.getTodoList(today).await()
                todoCategoryRecyclerViewAdapter = TodoCategoryRecyclerViewAdapter(
                    (result[0] as ArrayList<ArrayList<ToDoData>>),
                    (result[1] as ArrayList<String>),
                    (result[2] as ArrayList<String>), context)
                todoCategoryRecyclerViewAdapter.setCalendarViewModel(calendarViewModel)
                todoCategoryRecycler.layoutManager = LinearLayoutManager(context)
                todoCategoryRecycler.adapter = todoCategoryRecyclerViewAdapter

                calendarViewModel.date.observe(findViewTreeLifecycleOwner()!!, Observer{
                    CoroutineScope(Dispatchers.Main).launch {
                        textTodoDate.text = "🗓️ ${format.format(it)}"
                        val result1 = sqlite.getTodoList(it).await()
                        if((result1[1] as ArrayList<String>).size == 0){
                            textAddTodo.visibility = View.VISIBLE
                        }else{
                            textAddTodo.visibility = View.GONE
                        }
                        todoCategoryRecyclerViewAdapter.setTodoDataSet(
                            (result1[0] as ArrayList<ArrayList<ToDoData>>),
                            (result1[1] as ArrayList<String>),
                            (result1[2] as ArrayList<String>))
                    }

                })
            }

            first = false
        }
    }

    fun setCalendarViewModel(model: CalendarViewModel){
        calendarViewModel = model
    }
}