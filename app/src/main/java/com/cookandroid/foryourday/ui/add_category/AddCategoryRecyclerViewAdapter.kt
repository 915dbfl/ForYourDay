package com.cookandroid.foryourday.ui.add_category

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.ColorData

class AddCategoryRecyclerViewAdapter(private var dataSet: List<ColorData>, context: Context, private var delete: Boolean)
    : RecyclerView.Adapter<AddCategoryRecyclerViewAdapter.ViewHolder>() {
    var selectedList = arrayListOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView
        val categoryColor: LinearLayout
        val categoryCheckBox: CheckBox

        init {
            textView = view.findViewById(R.id.todo_item_content)
            categoryColor = view.findViewById(R.id.category_color)
            categoryCheckBox = view.findViewById(R.id.todo_item_checkbox)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_recyclerview_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!delete){
            holder.categoryCheckBox.visibility = View.GONE
        }else{
            holder.categoryCheckBox.visibility = View.VISIBLE
            holder.categoryCheckBox.isChecked = false
        }
        holder.categoryCheckBox.setOnCheckedChangeListener { _, b ->
            if (b){
                selectedList.add(dataSet[position].id!!)
            }else{
                selectedList.remove(dataSet[position].id!!)
            }
        }
        val title = dataSet[position].title
        val value = dataSet[position].value
        holder.textView.text = title
        holder.categoryColor.setBackgroundColor(Color.parseColor(value))
    }

    override fun getItemCount() = dataSet.size

    fun setValue(newData: List<ColorData>?, newDalete: Boolean){
        if(newData != null){
            dataSet = newData
        }
        delete = newDalete
        notifyDataSetChanged()
    }

}