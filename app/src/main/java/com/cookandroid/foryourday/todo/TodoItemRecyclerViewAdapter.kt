package com.cookandroid.foryourday.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R

class TodoItemRecyclerViewAdapter(private val parentViewHolder: TodoCategoryRecyclerViewAdapter.ViewHolder, private val dataSet: ArrayList<String>, private val category: String, context: Context): RecyclerView.Adapter<TodoItemRecyclerViewAdapter.ViewHolder>() {
    private var cTaskCount = parentViewHolder.count_completed.text.toString().toInt()
    private var tTaskCount = parentViewHolder.count_todo.text.toString().toInt()
    private val context = context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView
        val categoryColor: LinearLayout
        val todoCheckbox: CheckBox

        init {
            textView = view.findViewById(R.id.todo_item_content)
            categoryColor = view.findViewById(R.id.category_color)
            todoCheckbox = view.findViewById(R.id.todo_item_checkbox)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_recyclerview_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //데이터베이스에 카테고리 접근 후 색깔 지정 필요
        holder.textView.text = dataSet[position]
        if(category.equals("중요")){
            holder.categoryColor.setBackgroundColor(
                ContextCompat.getColor(holder.categoryColor.context,
                R.color.category_important
            ))
        }else if(category.equals("루틴")){
            holder.categoryColor.setBackgroundColor(
                ContextCompat.getColor(holder.categoryColor.context,
                R.color.category_routine
            ))
        }

        holder.todoCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                Toast.makeText(context, "오늘 하루도 화이팅", Toast.LENGTH_SHORT).show()
                cTaskCount++
                tTaskCount--

            }else{
                cTaskCount--
                tTaskCount++
            }
            parentViewHolder.count_completed.text = cTaskCount.toString()
            parentViewHolder.count_todo.text = tTaskCount.toString()
        }
    }

    override fun getItemCount() = dataSet.size
}