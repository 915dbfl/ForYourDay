package com.cookandroid.foryourday.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.sqlite.DDayDBHelper
import com.cookandroid.foryourday.sqlite.SQLite
import com.cookandroid.foryourday.sqlite.ToDoDBHelper
import com.cookandroid.foryourday.ui.add_category.AddCategoryFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class DeleteCategoryDialog(val context: Context) {
    private val dlg = Dialog(context)
    private lateinit var btnDelete: AppCompatButton
    private lateinit var btnCancel: AppCompatButton
    private lateinit var list: ArrayList<Int>
    private var lastValue = 0
    private lateinit var fragment: AddCategoryFragment
    private val sqlite = SQLite(context)

    fun show(lst: ArrayList<Int>, addCategoryFragment: AddCategoryFragment){
        list = lst
        lastValue = list[list.size-1]
        fragment = addCategoryFragment
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_category_delete)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnDelete = dlg.findViewById(R.id.btn_delete)
        btnCancel = dlg.findViewById(R.id.btn_cancel)

        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                deleteTodos()
            }
            dlg.dismiss()
        }
        dlg.show()
    }

    private suspend fun deleteTodos(){
        val todoDb = ToDoDBHelper(context).writableDatabase
        val dDayDb = DDayDBHelper(context).writableDatabase
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        for(id in list){
            val sql1 = "select *  from TodosTable where categoryId = $id"
            val c1 = todoDb.rawQuery(sql1, null)
            if(c1.count > 0){
                while(c1.moveToNext()){
                    val idx = c1.getColumnIndex("id")
                    val id1 = c1.getInt(idx)
                    deleteTodo(id1, header)
                }
                todoDb.execSQL("delete from TodosTable where categoryId = $id")
            }
            c1.close()
            val sql2 = "select * from DDaysTable where categoryId = $id"
            val c2 =dDayDb.rawQuery(sql2, null)
            if(c2.count > 0){
                while(c2.moveToNext()){
                    val idx = c2.getColumnIndex("id")
                    val id2 = c2.getInt(idx)
                    Log.d("dfdf", "$id2 삭제 진행!")
                    deleteDDay(id2, header)
                }
                Log.d("ㄷㄹㄷㄹㄷㄹ", "디비 삭제 진행!")
                dDayDb.execSQL("delete from DDaysTable where categoryId = $id")
            }
            c2.close()
            deleteCategory(id, id == lastValue, header)
        }
        todoDb.close()
        dDayDb.close()
    }

    private fun deleteTodo(id: Int, header: String){
        ApiInterface.create().deleteTodo(header, id).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(!response.isSuccessful){
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
                        Log.d("deleteCategory", "error: $t")
                    }
                }
            }
        )
    }

    private fun deleteDDay(id: Int, header: String){
        ApiInterface.create().deleteDDay(header, id).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(!response.isSuccessful) {
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
                        Log.d("deleteDDay", "error: $t")
                    }
                }
            }
        )
    }

    private fun deleteCategory(id: Int, check: Boolean, header: String){
        ApiInterface.create().deleteCategory(header, id).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Main).launch {
                            sqlite.deleteCategoryDB(id)
                            if (check){
                                Toast.makeText(context, "삭제가 완료되었습니다!🙌", Toast.LENGTH_SHORT).show()
                                fragment.also {
                                    it.btnDeleteComplete!!.visibility = View.GONE
                                    it.btnDeleteCancel!!.visibility = View.GONE
                                    val categories = sqlite.getCategories().await()
                                    it.addCategoryRecyclerViewAdapter!!.setValue(categories, false)
                                    if(categories.isEmpty()){
                                        it.addCategoryMessage!!.visibility = View.VISIBLE
                                        it.btnAddCategory!!.visibility = View.VISIBLE
                                        it.btnDeleteCategory!!.visibility = View.GONE
                                    }else{
                                        it.btnDeleteCategory!!.visibility = View.VISIBLE
                                        it.btnAddCategory!!.visibility = View.VISIBLE
                                        it.addCategoryMessage!!.visibility = View.GONE
                                    }
                                }
                            }
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
                        Log.d("deleteCategory", "error: $t")
                    }
                }
            }
        )
    }
}