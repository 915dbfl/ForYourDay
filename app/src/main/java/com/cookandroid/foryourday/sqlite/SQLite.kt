package com.cookandroid.foryourday.sqlite

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.cookandroid.foryourday.main.MainActivity
import com.cookandroid.foryourday.retrofit.*
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SQLite(val context: Context){

    fun getCategories(): Deferred<List<CategoryData>> =
        CoroutineScope(Dispatchers.Default).async {
            val helper = CategoryDBHelper(context)
            val sql = "select * from CategoriesTable"
            val cur = helper.writableDatabase.rawQuery(sql, null)
            val result = arrayListOf<CategoryData>()

            while (cur.moveToNext()){
                val idx1 = cur.getColumnIndex("title")
                val idx2 = cur.getColumnIndex("value")
                val idx3 = cur.getColumnIndex("id")
                val title = cur.getString(idx1)
                val value = cur.getString(idx2)
                val id = cur.getInt(idx3)

                val data = CategoryData(title, value, id)
                result.add(data)
            }
            cur.close()
            helper.writableDatabase.close()
            return@async result
        }

    suspend fun getTodoList(date: Date) : Deferred<List<Any>> =
        CoroutineScope(Dispatchers.Default).async {
            val todos = ArrayList<ArrayList<ToDoData>>()
            val catData = ArrayList<String>()
            val valData = ArrayList<String>()
            val helper = ToDoDBHelper(context)
            val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val categories = getCategories().await()
            for(category in categories){
                val sql = "select * from TodosTable where date = '${format.format(date)}' and categoryId = ${category.id}"
                val c = helper.writableDatabase.rawQuery(sql, null)
                val data = ArrayList<ToDoData>()

                while(c.moveToNext()){
                    val idx1 = c.getColumnIndex("id")
                    val idx2 = c.getColumnIndex("content")
                    val idx3 = c.getColumnIndex("complete")

                    val id = c.getInt(idx1)
                    val content = c.getString(idx2)
                    val complete = c.getInt(idx3)

                    val todo = ToDoData(id, complete == 1, content, date.time, category.id!!)
                    data.add(todo)
                }

                if(data.size != 0){
                    todos.add(data)
                    valData.add(category.value)
                    catData.add(category.title)
                }

                c.close()
            }

            helper.writableDatabase.close()
            return@async arrayListOf(todos, catData, valData)
        }

    fun getUserInfo(): Deferred<UserData> =
        CoroutineScope(Dispatchers.Default).async {
            val helper = UserInfoDBHelper(context)
            val sql = "select * from UserTable"
            val c1 = helper.writableDatabase.rawQuery(sql, null)
            c1.moveToFirst()

            val idx1 = c1.getColumnIndex("userName")
            val idx2 = c1.getColumnIndex("userId")
            val idx3 = c1.getColumnIndex("accessToken")
            val idx4 = c1.getColumnIndex("email")
            val idx5 = c1.getColumnIndex("refreshToken")
            val idx6 = c1.getColumnIndex("imagePath")

            val userName = c1.getString(idx1)
            val userId = c1.getInt(idx2)
            val accessToken = c1.getString(idx3)
            val email = c1.getString(idx4)
            val refreshToken = c1.getString(idx5)
            val imagePath = c1.getString(idx6)

            val user = UserInfo(userId, email, userName, imagePath)
            val oauth = OAuthInfo(accessToken, refreshToken)
            c1.close()
            helper.writableDatabase.close()
            return@async UserData(oauth, user)
        }

    fun addTodoDB(todo: ToDoData){
        val helper = ToDoDBHelper(context)
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        val sql = """
            insert into TodosTable (id, date, content, complete, categoryId)
            values(?, ?, ?, ?, ?)
        """.trimIndent()

        val arg = arrayOf(todo.id, format.format(Date(todo.date)), todo.content, 0, todo.categoryId)
        helper.writableDatabase.execSQL(sql, arg)
        helper.writableDatabase.close()
    }

    fun addCategoryDB(data: CategoryData){
        val helper = CategoryDBHelper(context)
        val sql = """
            insert into CategoriesTable (id, title, value) values(?, ?, ?)
        """.trimIndent()
        val arg = arrayOf(data.id, data.title, data.value)
        helper.writableDatabase.execSQL(sql, arg)
        helper.writableDatabase.close()
    }

    fun addDDayDB(data: DDayData){
        val helper = DDayDBHelper(context)
        val sql = """
            insert into DDaysTable (id, userId, main, categoryId, ddate, content)
            values(?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val arg = arrayOf(data.id, data.userId, data.main, data.categoryId, data.ddate, data.content)
        helper.writableDatabase.execSQL(sql, arg)
        helper.writableDatabase.close()
    }

    fun deleteCategoryDB(id: Int){
        val db = CategoryDBHelper(context).writableDatabase
        val sql = "delete from CategoriesTable where id = $id"
        db.execSQL(sql)
        db.close()
    }

    fun makeCategoriesDB(token: String){
        val header = "bearerToken $token"
        val db = CategoryDBHelper(context).writableDatabase
        val sql = "insert into CategoriesTable (id, title, value) values(?, ?, ?)"

        ApiInterface.create().getCategories(header).enqueue(
            object : retrofit2.Callback<Categories>{
                override fun onResponse(call: Call<Categories>, response: Response<Categories>) {
                    if (response.isSuccessful){
                        for (c in response.body()!!.categories){
                            val arg = arrayOf(c.id, c.title, c.value)
                            db.execSQL(sql, arg)
                        }
                        db.close()
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Categories>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("makeCategoriesDB", "error: $t")
                    }
                }
            }
        )
    }

    fun makeTodoDB(token: String){
        val header = "bearerToken $token"
        val db = ToDoDBHelper(context).writableDatabase
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val sql = """
            insert into TodosTable (id, date, content, complete, categoryId)
            values(?, ?, ?, ?, ?)
        """.trimIndent()

        ApiInterface.create().getToDos(header).enqueue(
            object : retrofit2.Callback<ToDos>{
                override fun onResponse(call: Call<ToDos>, response: Response<ToDos>) {
                    if(response.isSuccessful){
                        for (c in response.body()!!.todos){
                            val arg = arrayOf(c.id, format.format(c.date), c.content, c.complete, c.categoryId)
                            db.execSQL(sql, arg)
                        }
                        db.close()
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ToDos>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("makeTodoDB", "error: $t")
                    }
                }
            }
        )
    }

    fun makeDDayDB(userData: UserData){
        val header = "bearerToken ${userData.oauth.accessToken}"
        val db = DDayDBHelper(context).writableDatabase
        val sql = """
            insert into DDaysTable (id, userId, main, categoryId, ddate, content)
            values(?, ?, ?, ?, ?, ?)
        """.trimIndent()

        ApiInterface.create().getDDays(header).enqueue(
            object : retrofit2.Callback<DDays>{
                override fun onResponse(call: Call<DDays>, response: Response<DDays>) {
                    if(response.isSuccessful){
                        for (c in response.body()!!.dDays){
                            val arg = arrayOf(c.id, c.userId, c.main, c.categoryId, c.ddate, c.content)
                            db.execSQL(sql, arg)
                        }
                        db.close()
                        val mainIntent = Intent(context, MainActivity::class.java)
                        context.startActivity(mainIntent)
                        Toast.makeText(context, "${userData.user.userName}님! 반갑습니다! 🤗", Toast.LENGTH_SHORT).show()
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<DDays>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("makeDDayDB", "error: $t")
                    }
                }
            }
        )
    }

    suspend fun getDDays(): Deferred<List<Any>> =
        CoroutineScope(Dispatchers.Default).async {
            val helper = DDayDBHelper(context)
            val dDays = ArrayList<ArrayList<DDayData>>()
            val catData = ArrayList<String>()
            val valData = ArrayList<String>()
            val categories = getCategories().await()

            for(category in categories){
                val sql = "select * from DDaysTable where categoryId = ${category.id}"
                val cur = helper.readableDatabase.rawQuery(sql, null)
                val data = ArrayList<DDayData>()

                while(cur.moveToNext()){
                    val idx1 = cur.getColumnIndex("id")
                    val idx2 = cur.getColumnIndex("userId")
                    val idx3 = cur.getColumnIndex("main")
                    val idx4 = cur.getColumnIndex("categoryId")
                    val idx5 = cur.getColumnIndex("ddate")
                    val idx6 = cur.getColumnIndex("content")

                    val id = cur.getInt(idx1)
                    val userId = cur.getInt(idx2)
                    val main = cur.getInt(idx3)
                    val categoryId = cur.getInt(idx4)
                    val ddate = cur.getString(idx5)
                    val content = cur.getString(idx6)

                    val dDay = DDayData(id, userId, main == 1, categoryId, ddate.toLong(), content)
                    data.add(dDay)
                }

                if(data.size != 0){
                    dDays.add(data)
                    valData.add(category.value)
                    catData.add(category.title)
                }
                cur.close()
            }
            helper.readableDatabase.close()
            return@async arrayListOf(dDays, catData, valData)
        }

    fun getMainDDays(): Deferred<ArrayList<DDayData>> =
        CoroutineScope(Dispatchers.Default).async {
            val helper = DDayDBHelper(context)
            val sql = """
                select * from DDaysTable where main = 1 order by ddate asc
            """.trimIndent()
            val cur = helper.readableDatabase.rawQuery(sql, null)
            val result = ArrayList<DDayData>()

            while(cur.moveToNext() and (result.size <= 2)){
                val idx1 = cur.getColumnIndex("id")
                val idx2 = cur.getColumnIndex("userId")
                val idx3 = cur.getColumnIndex("main")
                val idx4 = cur.getColumnIndex("categoryId")
                val idx5 = cur.getColumnIndex("ddate")
                val idx6 = cur.getColumnIndex("content")

                val id = cur.getInt(idx1)
                val userId = cur.getInt(idx2)
                val main = cur.getInt(idx3)
                val categoryId = cur.getInt(idx4)
                val ddate = cur.getString(idx5)
                val content = cur.getString(idx6)

                val dDay = DDayData(id, userId, main == 1, categoryId, ddate.toLong(), content)
                result.add(dDay)
            }
            cur.close()
            helper.readableDatabase.close()
            return@async result
        }

    fun getCategoryValue(categoryId: Int): Deferred<String> =
        CoroutineScope(Dispatchers.Default).async {
            val helper = CategoryDBHelper(context)
            val sql = """
                select * from CategoriesTable where id = $categoryId
            """.trimIndent()
            val cur = helper.readableDatabase.rawQuery(sql, null)
            cur.moveToFirst()

            val idx = cur.getColumnIndex("value")
            val value = cur.getString(idx)

            cur.close()
            helper.readableDatabase.close()
            return@async value
        }

    fun patchTodoDB(toDoData: ToDoData){
        val helper = ToDoDBHelper(context)
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val sql = """
            update TodosTable set complete = ${toDoData.complete.compareTo(false)}, date = '${format.format(Date(toDoData.date))}', 
             content = '${toDoData.content}', categoryId = ${toDoData.categoryId} where id = ${toDoData.id}
        """.trimIndent()
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    fun patchDDayDB(dDayData: DDayData){
        val helper = DDayDBHelper(context)
        val sql = """
            update DDaysTable set main = ${dDayData.main.compareTo(false)}, categoryId = ${dDayData.categoryId}, ddate = '${dDayData.ddate}', 
            content = '${dDayData.content}' where id = ${dDayData.id}
        """.trimIndent()
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    fun patchUserName(name: String){
        val helper = UserInfoDBHelper(context)
        val sql = """
            update UserTable set userName = '$name' 
        """.trimIndent()
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    fun patchUserProfileImage(path: String?){
        val helper = UserInfoDBHelper(context)
        val sql = """
            update UserTable set imagePath = '$path'
        """.trimIndent()
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    fun deleteUser(){
        deleteTodos()
        deleteUserInfo()
        deleteCategories()
        deleteDDays()
    }

    private fun deleteTodos(){
        val helper = ToDoDBHelper(context)
        val sql = "delete from TodosTable"
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    private fun deleteDDays(){
        val helper = DDayDBHelper(context)
        val sql = "delete from DDaysTable"
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    private fun deleteCategories(){
        val helper = CategoryDBHelper(context)
        val sql = "delete from CategoriesTable"
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }

    private fun deleteUserInfo(){
        val helper = UserInfoDBHelper(context)
        val sql = "delete from UserTable"
        helper.writableDatabase.execSQL(sql)
        helper.writableDatabase.close()
    }
}
