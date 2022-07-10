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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.findNavController
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.d_day.DDayFuncs
import com.cookandroid.foryourday.d_day.DDayViewModel
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.DDayData
import com.cookandroid.foryourday.sqlite.DDayDBHelper
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DDayDetailDialog(private val context: Context, private val view: View) {
    private val dlg = Dialog(context)
    private lateinit var btnDDayDelete: AppCompatButton
    private lateinit var btnDDayModify: AppCompatButton
    private lateinit var textViewDDayLabel: TextView
    private lateinit var textViewDDayDate: TextView
    private lateinit var textViewDDayCategory: TextView
    private lateinit var categoryColor: LinearLayout
    private lateinit var textViewDDay: TextView
    private val sqlite = SQLite(context)

    fun start(position: Int, data: DDayData, category: String, color: String){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_d_day)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnDDayDelete = dlg.findViewById(R.id.btn_d_day_delete)
        btnDDayModify = dlg.findViewById(R.id.btn_d_day_modify)

        textViewDDay = dlg.findViewById(R.id.textview_d_day)
        textViewDDayCategory = dlg.findViewById(R.id.textview_d_day_category)
        textViewDDayDate = dlg.findViewById(R.id.textview_d_day_date)
        textViewDDayLabel = dlg.findViewById(R.id.textview_d_day_label)
        categoryColor = dlg.findViewById(R.id.category_color)

        textViewDDayLabel.text = data.content
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val date = format.format(Date(data.ddate))
        textViewDDayDate.text = date
        textViewDDayCategory.text = category
        categoryColor.setBackgroundColor(Color.parseColor(color))
        textViewDDay.text = DDayFuncs().getDDay(date)

        btnDDayDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                deleteDDay(data.id!!)
                deleteDDayDb(data.id)
                val dDayViewModel = ViewModelProvider(context as ViewModelStoreOwner)[DDayViewModel::class.java]
                dDayViewModel.setUpdatePosition(position)
                Toast.makeText(context, "삭제가 완료되었습니다! 🤗", Toast.LENGTH_SHORT).show()
                dlg.dismiss()
            }
        }

        btnDDayModify.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("modifyDDay", data)
            bundle.putSerializable("position", position)
            view.findNavController().navigate(R.id.nav_add_d_day, bundle)
            dlg.dismiss()
        }

        dlg.show()
    }

    private suspend fun deleteDDay(id: Int){
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().deleteDDay(header, id).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(!response.isSuccessful){
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Log.d("deleteDDay", jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"))
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("deleteDDay", "error: $t")
                }
            }
        )
    }

    private fun deleteDDayDb(id: Int){
        val helper = DDayDBHelper(context)
        helper.writableDatabase.execSQL("delete from DDaysTable where id = $id")
        helper.close()
    }
}