package com.cookandroid.foryourday.ui.add_todo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.CategoryData

class AddToDoRecyclerViewAdapter(private val dataSet: List<CategoryData>)
    : RecyclerView.Adapter<AddToDoRecyclerViewAdapter.ViewHolder>() {
    var selectedCategoryId = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.category_item)
        val categoryColor: LinearLayout = view.findViewById(R.id.category_color)
        val radioButton: RadioButton = view.findViewById(R.id.category_radio_btn)

        init {
            radioButton.setOnClickListener {
                selectedCategoryId = dataSet[absoluteAdapterPosition].id!!
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.radio_btn_recyclerview, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = dataSet[position].title
        val value = dataSet[position].value
        holder.textView.text = title
        holder.radioButton.contentDescription = "category_$position"
        holder.categoryColor.setBackgroundColor(Color.parseColor(value))
        holder.radioButton.isChecked = dataSet[position].id == selectedCategoryId
    }

    override fun getItemCount() = dataSet.size

}