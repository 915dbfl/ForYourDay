package com.cookandroid.foryourday.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R

class TodoCategoryRecyclerViewAdapter(private val dataSet: ArrayList<HashMap<String, ArrayList<String>>>, context: Context): RecyclerView.Adapter<TodoCategoryRecyclerViewAdapter.ViewHolder>() {
    val context = context

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val category: TextView
        val count_completed: TextView
        val count_todo: TextView
        val category_item_recycler: RecyclerView

        init{
            category = view.findViewById(R.id.category_id)
            count_completed = view.findViewById(R.id.task_completed)
            count_todo = view.findViewById(R.id.task_todo)
            category_item_recycler = view.findViewById(R.id.todo_item_recycler)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_recyclerview_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = dataSet[position].keys.elementAt(0).toString()
        holder.count_todo.text = dataSet[position][key]!!.size.toString()
        val item_adapter = TodoItemRecyclerViewAdapter(holder, dataSet[position][key]!!, key, context)
        holder.category.text = key
        holder.category_item_recycler.layoutManager = LinearLayoutManager(holder.category_item_recycler.context)
        holder.category_item_recycler.adapter = item_adapter
    }

    override fun getItemCount() = dataSet.size
}