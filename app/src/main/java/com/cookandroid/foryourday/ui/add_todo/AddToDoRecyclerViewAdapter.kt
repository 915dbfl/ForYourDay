package com.cookandroid.foryourday.ui.add_todo

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.ColorData

class AddToDoRecyclerViewAdapter(private val dataSet: List<ColorData>, context: Context)
    : RecyclerView.Adapter<AddToDoRecyclerViewAdapter.ViewHolder>() {
    var selelctedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView
        val categoryColor: LinearLayout
        val radioButton: RadioButton

        init {
            textView = view.findViewById(R.id.category_item)
            categoryColor = view.findViewById(R.id.category_color)
            radioButton = view.findViewById(R.id.category_radio_btn)

            radioButton.setOnClickListener {
                selelctedPosition = absoluteAdapterPosition
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
        holder.categoryColor.setBackgroundColor(Color.parseColor(value))
        holder.radioButton.isChecked = (position == selelctedPosition)
    }

    override fun getItemCount() = dataSet.size
}