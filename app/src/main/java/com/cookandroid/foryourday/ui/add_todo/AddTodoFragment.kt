package com.cookandroid.foryourday.ui.add_todo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarView
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.databinding.FragmentAddTodoBinding
import com.cookandroid.foryourday.retrofit.*
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

class AddTodoFragment :androidx.fragment.app.Fragment() {
    private val addTodoViewModel: AddTodoViewModel by activityViewModels()
    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private lateinit var categoryRecyclerViewAdapter: AddToDoRecyclerViewAdapter
    private var _binding: FragmentAddTodoBinding? = null
    private var dateData: Date? = null
    private lateinit var cal: CalendarView
    private val binding get() = _binding!!
    private var modifyData: ToDoData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)

        modifyData = arguments?.getSerializable("modifyTodo") as ToDoData?
        val calInstance = Calendar.getInstance()
        cal = binding.addTodoCal
        cal.setIsPicker(1)
        cal.setCalendarViewModel(calendarViewModel)
        cal.updateCalendar(calInstance, calInstance.time)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todoDate = binding.textviewTodoDate
        val btnComplete = binding.btnAddTodoComplete
        val btnModifyToDo = binding.btnModifyTodo
        val btnCancel = binding.btnAddTodoCancel
        val categoryRecycler = binding.categoryRecyclerview
        val textViewAddCategory = binding.textviewAddCategory
        val edtTodoLabel = binding.edtTodoLabel
        val sqlite = SQLite(context!!)

        CoroutineScope(Dispatchers.Main).launch {
            val categories = sqlite.getCategories().await()

            categoryRecyclerViewAdapter = AddToDoRecyclerViewAdapter(categories)
            categoryRecycler.layoutManager = LinearLayoutManager(context)
            categoryRecycler.adapter = categoryRecyclerViewAdapter

            if(categories.isEmpty()){
                textViewAddCategory.visibility = View.VISIBLE
            }

            if (modifyData != null){
                btnComplete.visibility = View.GONE
                btnModifyToDo.visibility = View.VISIBLE
                calendarViewModel.updatePickerDate(Date(modifyData!!.date))
                edtTodoLabel.setText(modifyData!!.content)
                categoryRecyclerViewAdapter.selectedCategoryId = modifyData!!.categoryId
                categoryRecyclerViewAdapter.notifyDataSetChanged()
            }else{
                edtTodoLabel.text = null
            }
        }

        calendarViewModel.pickerDate.observe(viewLifecycleOwner, {
            dateData = it
            val date = SimpleDateFormat("yyyy.M.dd", Locale.getDefault()).format(it)
            addTodoViewModel.updateToDoDate(date)
        })

        addTodoViewModel.text.observe(viewLifecycleOwner, {
            todoDate.text = it
        })

        btnComplete.setOnClickListener {
            val date = dateData!!.time
            val label = edtTodoLabel.text.toString()
            when {
                label.replace(" ", "") == "" -> {
                    Toast.makeText(context, "투두 이름을 설정해주세요!😊", Toast.LENGTH_SHORT).show()
                }
                categoryRecyclerViewAdapter.selectedCategoryId == -1 -> {
                    Toast.makeText(context, "카테고리를 설정해주세요!😊", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val todoData = ToDoData(null, false, label, date, categoryRecyclerViewAdapter.selectedCategoryId)
                    CoroutineScope(Dispatchers.Default).launch {
                        postApi(todoData)
                    }

                }
            }
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack(R.id.nav_home, true)
            findNavController().navigate(R.id.nav_home)
        }

        textViewAddCategory.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.nav_add_category)
        }

        btnModifyToDo.setOnClickListener {
            val date = dateData!!.time
            val todoData = ToDoData(modifyData!!.id, modifyData!!.complete, edtTodoLabel.text.toString(), date, categoryRecyclerViewAdapter.selectedCategoryId)
            CoroutineScope(Dispatchers.Default).launch {
                patchApi(todoData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun patchApi(todoData: ToDoData){
        val sqlite = SQLite(context!!)
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().patchTodo(header, todoData.id!!, todoData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Main).launch {
                            sqlite.patchTodoDB(todoData)
                        }
                        Toast.makeText(context, "수정이 완료되었습니다!🤗", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.nav_home, true)
                        findNavController().navigate(R.id.nav_home)
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

    private suspend fun postApi(todoData: ToDoData){
        val sqlite = SQLite(context!!)
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().addTodo(header, todoData).enqueue(
            object : retrofit2.Callback<ToDo>{
                override fun onResponse(call: Call<ToDo>, response: Response<ToDo>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Default).launch {
                            sqlite.addTodoDB(response.body()!!.todo)
                        }
                        Toast.makeText(context, "투두가 추가되었습니다!😊", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.nav_home, true)
                        findNavController().navigate(R.id.nav_home)
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ToDo>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("todoPostApi", "error: $t")
                    }
                }
            }
        )
    }
}