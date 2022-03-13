package com.cookandroid.foryourday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DdayRecyclerAdapter(private val dataSet: ArrayList<ArrayList<String>>): RecyclerView.Adapter<DdayRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val dDayAmount: TextView
        val dDayContent: TextView
        val categoryColor: LinearLayout

        init {
            dDayAmount = view.findViewById(R.id.d_day_amount)
            categoryColor = view.findViewById(R.id.category_color)
            dDayContent = view.findViewById(R.id.d_day_content)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DdayRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.d_day_recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DdayRecyclerAdapter.ViewHolder, position: Int) {
        holder.dDayAmount.text = dataSet[position][0]
        holder.dDayContent.text = dataSet[position][1]
        if(dataSet[position][2].equals("중요")){
            holder.categoryColor.setBackgroundColor(
                ContextCompat.getColor(holder.categoryColor.context,
                R.color.category_important
            ))
        }else if(dataSet[position][2].equals("루틴")){
            holder.categoryColor.setBackgroundColor(
                ContextCompat.getColor(holder.categoryColor.context,
                R.color.category_routine
            ))
        }
    }

    override fun getItemCount() = dataSet.size
}