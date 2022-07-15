package com.cookandroid.foryourday.ui.add_d_day

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.calendar.CalendarView
import com.cookandroid.foryourday.calendar.CalendarViewModel
import com.cookandroid.foryourday.d_day.DDayFuncs
import com.cookandroid.foryourday.d_day.DDayViewModel
import com.cookandroid.foryourday.databinding.FragmentAddDDayBinding
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.DDay
import com.cookandroid.foryourday.retrofit.DDayData
import com.cookandroid.foryourday.sqlite.SQLite
import com.cookandroid.foryourday.ui.add_todo.AddToDoRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddDDayFragment: Fragment() {
    private val addDDayViewModel: AddDDayViewModel by activityViewModels()
    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val dDayViewModel: DDayViewModel by activityViewModels()
    private var _binding: FragmentAddDDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var cal: CalendarView
    private var dateData: Date? = null
    private lateinit var categoryRecyclerViewAdapter: AddToDoRecyclerViewAdapter
    private var modifyData: DDayData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddDDayBinding.inflate(inflater, container, false)

        modifyData = arguments?.getSerializable("modifyDDay") as DDayData?
        val calInstance = Calendar.getInstance()
        cal = binding.addDDayCal
        cal.setIsPicker(1)
        cal.setCalendarViewModel(calendarViewModel)
        cal.updateCalendar(calInstance, calInstance.time)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dDayDate = binding.textviewDDayDate
        val btnComplete = binding.btnAddDDayComplete
        val btnCancel = binding.btnAddDDayCancel
        val categoryRecycler = binding.categoryRecyclerview
        val textViewAddCategory = binding.textviewAddCategory
        val dDay = binding.textviewDDay
        val edtDDayLabel = binding.edtDDayLabel
        val dDayCheckMain = binding.dDayCheckMain
        val sqlite = SQLite(context!!)
        val btnDDayModify = binding.btnDDayModify

        CoroutineScope(Dispatchers.Main).launch {
            val categories = sqlite.getCategories().await()
            categoryRecyclerViewAdapter = AddToDoRecyclerViewAdapter(categories)
            categoryRecycler.layoutManager = LinearLayoutManager(context)
            categoryRecycler.adapter = categoryRecyclerViewAdapter

            if(categories.isEmpty()){
                textViewAddCategory.visibility = View.VISIBLE
            }

            if(modifyData != null){
                val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val dDayFuncs = DDayFuncs()
                btnComplete.visibility = View.GONE
                btnDDayModify.visibility = View.VISIBLE
                calendarViewModel.updatePickerDate(Date(modifyData!!.ddate))
                dDay.text = dDayFuncs.getDDay(format.format(Date(modifyData!!.ddate)))
                edtDDayLabel.setText(modifyData!!.content)
                categoryRecyclerViewAdapter.selectedCategoryId = modifyData!!.categoryId
                categoryRecyclerViewAdapter.notifyDataSetChanged()
                dDayCheckMain.isChecked = modifyData!!.main
            }else{
                edtDDayLabel.text = null
            }
        }

        calendarViewModel.pickerDate.observe(viewLifecycleOwner, {
            dateData = it
            val date = SimpleDateFormat("yyyy.M.dd", Locale.getDefault()).format(it)
            addDDayViewModel.updateDDayDate(date)
        })

        addDDayViewModel.text.observe(viewLifecycleOwner, {
            val dDayFuncs = DDayFuncs()
            dDayDate.text = it
            dDay.text = dDayFuncs.getDDay(it)
        })

        btnComplete.setOnClickListener {
            val label = edtDDayLabel.text.toString()
            when {
                label.replace(" ", "") == "" -> {
                    Toast.makeText(context, "디데이 이름을 설정해주세요!😊", Toast.LENGTH_SHORT).show()
                }
                categoryRecyclerViewAdapter.selectedCategoryId == -1 -> {
                    Toast.makeText(context, "카테고리를 설정해주세요!😊", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val date = dateData!!.time
                    val dDayData = DDayData(null, null, dDayCheckMain.isChecked, categoryRecyclerViewAdapter.selectedCategoryId, date, label)
                    CoroutineScope(Dispatchers.Default).launch {
                        postApi(dDayData)
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

        btnDDayModify.setOnClickListener {
            val date = dateData!!.time
            val dDayData = DDayData(modifyData!!.id, modifyData!!.userId, dDayCheckMain.isChecked, categoryRecyclerViewAdapter.selectedCategoryId, date, edtDDayLabel.text.toString())
            CoroutineScope(Dispatchers.Default).launch {
                patchApi(dDayData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun patchApi(dDayData: DDayData){
        val sqlite = SQLite(context!!)
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().patchDDay(header, dDayData.id!!, dDayData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Main).launch {
                            sqlite.patchDDayDB(dDayData)
                            dDayViewModel.setUpdatePosition(0)
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
                        Log.d("patchDDayApi", "error: $t")
                    }
                }
            }
        )
    }

    private suspend fun postApi(dDayData: DDayData){
        val sqlite = SQLite(context!!)
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().addDDay(header, dDayData).enqueue(
            object : retrofit2.Callback<DDay>{
                override fun onResponse(call: Call<DDay>, response: Response<DDay>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Default).launch {
                            sqlite.addDDayDB(response.body()!!.data)
                            dDayViewModel.setUpdatePosition(0)
                        }
                        Toast.makeText(context, "디데이가 추가되었습니다!😊", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.nav_home, true)
                        findNavController().navigate(R.id.nav_home)
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<DDay>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("dDayPostApi", "error: $t")
                    }
                }
            }
        )
    }
}