package com.cookandroid.foryourday.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.findNavController
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.ToDoData
import com.cookandroid.foryourday.sqlite.ToDoDBHelper
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TodoDetailDialog(private val context: Context, private val view: View) {
    private val dlg = Dialog(context)
    private lateinit var btnTodoDelete:AppCompatButton
    private lateinit var btnTodoModify:AppCompatButton
    private lateinit var textViewTodoLabel: TextView
    private lateinit var textViewTodoDate: TextView
    private lateinit var textViewTodoCategory: TextView
    private lateinit var categoryColor: LinearLayout
    private val sqlite = SQLite(context)
    private lateinit var calendarViewModel: CalendarViewModel

    fun start(data: ToDoData, category: String, color: String){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_todo)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        textViewTodoLabel = dlg.findViewById(R.id.textview_todo_label)
        textViewTodoDate = dlg.findViewById(R.id.textview_todo_date)
        textViewTodoCategory = dlg.findViewById(R.id.textview_todo_category)
        categoryColor = dlg.findViewById(R.id.category_color)

        btnTodoModify = dlg.findViewById(R.id.btn_todo_modify)
        btnTodoDelete = dlg.findViewById(R.id.btn_todo_delete)

        textViewTodoLabel.text = data.content
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        textViewTodoDate.text = format.format(Date(data.date))
        textViewTodoCategory.text = category
        categoryColor.setBackgroundColor(Color.parseColor(color))

        btnTodoModify.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("modifyTodo", data)
            view.findNavController().navigate(R.id.nav_add_todo, bundle)
            dlg.dismiss()
        }

        btnTodoDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                deleteTodo(data)
            }
            dlg.dismiss()
        }

        dlg.show()
    }

   private suspend fun deleteTodo(data: ToDoData){
       val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().deleteTodo(header, data.id!!).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful) {
                        deleteDB(data.id)
                        calendarViewModel.updateDate(Date(data.date))
                        calendarViewModel.updateCalendar()
                        Toast.makeText(context, "삭제가 완료되었습니다!🙌", Toast.LENGTH_SHORT).show()
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
                        Log.d("deleteTodo", "error: $t")
                    }
                }
            }
        )
    }

    private fun deleteDB(id: Int){
        val helper = ToDoDBHelper(context)
        helper.writableDatabase.execSQL("delete from TodosTable where id = $id")
        helper.close()
    }

    fun setCalendarViewModel(model: CalendarViewModel){
        calendarViewModel = model
    }
}