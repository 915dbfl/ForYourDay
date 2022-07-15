package com.cookandroid.foryourday.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.sqlite.SQLite
import com.cookandroid.foryourday.sqlite.ToDoDBHelper
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(private val context: Context, private val days: ArrayList<Date>, private val inputMonth: Int, private val calendarViewModel: CalendarViewModel, private val type: Int): RecyclerView.Adapter<CalendarAdapter.ViewHolder>(){
    private var total = 0
    private lateinit var valueDataSet: ArrayList<Int>
    private lateinit var countsDataSet: ArrayList<Int>
    private val dbFuns = SQLite(context)
    var pos = 0
    private var cur = 0

    fun getItem(position: Int) = days[position]

    fun getPosition(day: Date) = days.indexOf(day)

    init {
        val today = Calendar.getInstance()
        today.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        cur = days.indexOf(today.time)
        val calInstance = Calendar.getInstance()
        if (type == 1){
            calendarViewModel.updatePickerDate(Calendar.getInstance().time)
            calInstance.time = calendarViewModel.pickerDate.value!!
        }else{
            calInstance.time = calendarViewModel.date.value!!
        }
        val select = calInstance.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        pos = days.indexOf(select.time)
        if(type == 0){
            calendarViewModel.setCalPosition(pos)
        }else if(type == -1){
            calendarViewModel.setWeeklyPosition(pos)
        }
    }

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val pieChart = view.findViewById<PieChart>(R.id.cal_date_piechart)

        init {
            pieChart.setNoDataText(" ")
            view.setOnClickListener {
                val newIndex = it.tag as Int
                val newDate = getItem(newIndex)
                val oldIndex = pos
                this@CalendarAdapter.pos = newIndex
                if(type == 1){
                    calendarViewModel.updatePickerDate(newDate)
                    notifyItemChanged(newIndex)
                    notifyItemChanged(oldIndex)

                }else {
                    calendarViewModel.updateDate(newDate)
                    calendarViewModel.updateCalendar()
                }
            }
        }

        suspend fun bind(position: Int){
            val date = getItem(position)

            view.tag = position

            val calendar = Calendar.getInstance()

            total = 0

            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val day = calendar.get(Calendar.DATE)
            val month = calendar.get(Calendar.MONTH)

            val today = Date()
            val calToday = Calendar.getInstance()
            calToday.time = today

            CoroutineScope(Dispatchers.Main).launch {
                getDateTodo(date)
                val dataSet = setPieEntry()
                val pieDataSet = PieDataSet(dataSet,"todos")

                pieDataSet.apply {
                    colors = valueDataSet
                    setDrawValues(false)
                    valueTextSize = 16f
                }

                val pieData = PieData(pieDataSet)
                pieChart.apply {
                    data = pieData
                    setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                    centerText = day.toString()
                    description.isEnabled = false
                    setUsePercentValues(true)
                    isRotationEnabled = false
                    setDrawEntryLabels(false)
                    setTouchEnabled(false)
                    legend.isEnabled = false
                    holeRadius = 75F
                    setEntryLabelColor(Color.BLACK)
                    if(position == pos){
                        setHoleColor(Color.parseColor("#8CEDE865"))
                    }else{
                        setHoleColor(Color.WHITE)
                    }

                    if(month != inputMonth || year!= calToday.get(Calendar.YEAR)){
                        setCenterTextColor(Color.parseColor("#C4C4C4"))
                    }else if(position == cur){
                        setCenterTextColor(Color.parseColor("#56a6a9"))
                    }
                }

                pieChart.invalidate()
            }

        }
    }

    private suspend fun getDateTodo(date: Date){
        val valData = ArrayList<Int>()
        val cntsData = ArrayList<Int>()
        val helper = ToDoDBHelper(context)
        val d = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)
        val categories = dbFuns.getCategories().await()

        for(category in categories){
            val sql = "select * from TodosTable where date = '$d' and categoryId = ${category.id} and complete = 1"
            val c = helper.readableDatabase.rawQuery(sql, null)
            if (c.count != 0){
                valData.add(Color.parseColor(category.value))
                cntsData.add(c.count)
                total += c.count
            }
            c.close()
        }
        helper.readableDatabase.close()
        valueDataSet = valData
        countsDataSet = cntsData
    }


    private fun setPieEntry(): ArrayList<PieEntry>{
        val dataSet = ArrayList<PieEntry>()
        for(c in countsDataSet){
            val p = c.toFloat()/total*100
            dataSet.add(PieEntry(p))
        }
        return dataSet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            holder.bind(position)
        }
    }

    override fun getItemCount() = days.size

    fun todoCheckUpdate(position: Int){
        notifyItemChanged(position)
    }
}