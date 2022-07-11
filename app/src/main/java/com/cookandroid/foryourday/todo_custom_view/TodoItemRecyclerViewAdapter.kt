package com.cookandroid.foryourday.todo_custom_view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.dialog.TodoDetailDialog
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.ToDoData
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import kotlin.collections.ArrayList

class TodoItemRecyclerViewAdapter(private val parentViewHolderTodo: TodoCategoryRecyclerViewAdapter.ViewHolder, private val category: String, private val categoryVal: String, private val dataSet: ArrayList<ToDoData>, private val context: Context): RecyclerView.Adapter<TodoItemRecyclerViewAdapter.ViewHolder>() {
    private var cTaskCount = 0
    private var tTaskCount = 0
    private lateinit var calendarViewModel: CalendarViewModel

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.todo_item_content)
        val categoryColor: LinearLayout = view.findViewById(R.id.category_color)
        val todoCheckbox: CheckBox = view.findViewById(R.id.todo_item_checkbox)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_recyclerview_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].content
        if(dataSet[position].complete){
            cTaskCount++
        }else{
            tTaskCount++
        }
        parentViewHolderTodo.countCompleted.text = cTaskCount.toString()
        parentViewHolderTodo.countTodo.text = tTaskCount.toString()
        holder.todoCheckbox.isChecked = dataSet[position].complete
        holder.categoryColor.setBackgroundColor(Color.parseColor(categoryVal))

        holder.todoCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                cTaskCount++
                tTaskCount--

            }else{
                cTaskCount--
                tTaskCount++
            }
            parentViewHolderTodo.countCompleted.text = cTaskCount.toString()
            parentViewHolderTodo.countTodo.text = tTaskCount.toString()
            val newData = dataSet[position]
            newData.complete = isChecked
            CoroutineScope(Dispatchers.Default).launch {
                patchTodoApi(newData)
            }
        }

        holder.textView.setOnClickListener {
            val dlg = TodoDetailDialog(context, it)
            dlg.setCalendarViewModel(calendarViewModel)
            dlg.start(dataSet[position], category, categoryVal)
        }
    }

    override fun getItemCount() = dataSet.size

    private suspend fun patchTodoApi(toDoData: ToDoData){
        val sqlite = SQLite(context)
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().patchTodo(header, toDoData.id!!, toDoData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Main).launch {
                            val calPosition = calendarViewModel.calPosition.value
                            val weeklyPosition = calendarViewModel.weeklyPosition.value
                            sqlite.patchTodoDB(toDoData)
                            calendarViewModel.calAdapter.value!!.todoCheckUpdate(calPosition!!)
                            calendarViewModel.weeklyAdapter.value!!.todoCheckUpdate(weeklyPosition!!)
                        }
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("patchTodoApi", "error: $t")
                    }
                }
            }
        )
    }

    fun setCalendarViewModel(model: CalendarViewModel){
        calendarViewModel = model
    }
}