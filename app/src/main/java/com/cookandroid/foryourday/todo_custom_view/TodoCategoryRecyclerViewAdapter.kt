package com.cookandroid.foryourday.todo_custom_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.retrofit.ToDoData
import kotlin.collections.ArrayList

class TodoCategoryRecyclerViewAdapter(private var todoDataSet: ArrayList<ArrayList<ToDoData>>, private var catDataSet: List<String>, private var valDataSet: List<String>, private val context: Context): RecyclerView.Adapter<TodoCategoryRecyclerViewAdapter.ViewHolder>() {
    private lateinit var calendarViewModel: CalendarViewModel

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val category: TextView = view.findViewById(R.id.category_id)
        val countCompleted: TextView = view.findViewById(R.id.task_completed)
        val countTodo: TextView = view.findViewById(R.id.task_todo)
        val categoryItemRecycler: RecyclerView = view.findViewById(R.id.todo_item_recycler)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_recyclerview_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemAdapter = TodoItemRecyclerViewAdapter(holder, catDataSet[position], valDataSet[position], todoDataSet[position], context)
        itemAdapter.setCalendarViewModel(calendarViewModel)
        holder.category.text = catDataSet[position]
        holder.categoryItemRecycler.layoutManager = LinearLayoutManager(holder.categoryItemRecycler.context)
        holder.categoryItemRecycler.adapter = itemAdapter
    }

    override fun getItemCount(): Int {
        return catDataSet.size
    }

    fun setTodoDataSet(newTodo: ArrayList<ArrayList<ToDoData>>, newCat: ArrayList<String>, newVal: ArrayList<String>){
        todoDataSet = newTodo
        catDataSet = newCat
        valDataSet = newVal
        notifyDataSetChanged()
    }

    fun setCalendarViewModel(model: CalendarViewModel){
        calendarViewModel = model
    }
}