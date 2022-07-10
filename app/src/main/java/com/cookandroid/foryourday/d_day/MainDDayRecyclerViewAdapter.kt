package com.cookandroid.foryourday.d_day

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.DDayData
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainDDayRecyclerViewAdapter(private val dataSet: ArrayList<DDayData>, private val context: Context): RecyclerView.Adapter<MainDDayRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val dDayAmount: TextView = view.findViewById(R.id.d_day_amount)
        val dDayContent: TextView = view.findViewById(R.id.d_day_content)
        val categoryColor: LinearLayout = view.findViewById(R.id.category_color)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.d_day_recyclerview_main_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dDayContent.text = dataSet[position].content
        val data = Date(dataSet[position].ddate)
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val date = format.format(data)
        holder.dDayAmount.text = DDayFuncs().getDDay(date)

        CoroutineScope(Dispatchers.Main).launch {
            val sqlite = SQLite(context)
            holder.categoryColor.setBackgroundColor(Color.parseColor(sqlite.getCategoryValue(dataSet[position].categoryId).await()))
        }

    }

    override fun getItemCount() = dataSet.size
}