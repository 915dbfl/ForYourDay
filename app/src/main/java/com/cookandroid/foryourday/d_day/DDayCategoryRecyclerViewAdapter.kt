package com.cookandroid.foryourday.d_day

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.DDayData

class DDayCategoryRecyclerViewAdapter(private var dDayDataSet: ArrayList<ArrayList<DDayData>>, private var catDataSet: List<String>, private var valDataSet: List<String>, private val context: Context): RecyclerView.Adapter<DDayCategoryRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val category: TextView = view.findViewById(R.id.category_id)
        val dDayItemRecycler: RecyclerView = view.findViewById(R.id.d_day_item_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.d_day_recyclerview_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemAdapter = DDayItemRecyclerViewAdapter(catDataSet[position], valDataSet[position], dDayDataSet[position], context)
        holder.category.text = "🏃️ ${catDataSet[position]}"
        holder.dDayItemRecycler.layoutManager = LinearLayoutManager(holder.dDayItemRecycler.context)
        holder.dDayItemRecycler.adapter = itemAdapter
    }

    override fun getItemCount(): Int {
        return dDayDataSet.size
    }

}