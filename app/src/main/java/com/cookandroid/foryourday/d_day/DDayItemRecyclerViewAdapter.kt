package com.cookandroid.foryourday.d_day

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.dialog.DDayDetailDialog
import com.cookandroid.foryourday.retrofit.DDayData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DDayItemRecyclerViewAdapter(private val category: String, private val categoryVal: String, private val dataSet: ArrayList<DDayData>, private val context: Context): RecyclerView.Adapter<DDayItemRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val dDayAmount: TextView = view.findViewById(R.id.d_day_amount)
        val dDayContent: TextView = view.findViewById(R.id.d_day_content)
        val categoryColor: LinearLayout = view.findViewById(R.id.category_color)
        val imgCheckMain: ImageView = view.findViewById(R.id.img_check_main)
        val layoutDDay: ConstraintLayout = view.findViewById(R.id.layout_d_day)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.d_day_recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dDayContent.text = dataSet[position].content
        holder.categoryColor.setBackgroundColor(Color.parseColor(categoryVal))
        if(dataSet[position].main){
            holder.imgCheckMain.visibility = View.VISIBLE
        }else{
            holder.imgCheckMain.visibility = View.INVISIBLE
        }
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        holder.dDayAmount.text = DDayFuncs().getDDay(format.format(Date(dataSet[position].ddate)))

        holder.layoutDDay.setOnClickListener {
            val dlg = DDayDetailDialog(context, it)
            dlg.start(position, dataSet[position], category, categoryVal)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}