package com.cookandroid.foryourday.ui.add_category

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.ColorData

class AddCategoryRecyclerViewAdapter(private var dataSet: List<ColorData>, context: Context)
    : RecyclerView.Adapter<AddCategoryRecyclerViewAdapter.ViewHolder>() {
    private val context = context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView
        val categoryColor: LinearLayout
        val radioButton: RadioButton

        init {
            textView = view.findViewById(R.id.category_item)
            categoryColor = view.findViewById(R.id.category_color)
            radioButton = view.findViewById(R.id.category_radio_btn)
            radioButton.visibility = View.GONE

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
    }

    override fun getItemCount() = dataSet.size

    fun setData(newData: List<ColorData>){
        dataSet = newData
        Log.d("dataset", dataSet.toString())
        notifyDataSetChanged()
    }
}