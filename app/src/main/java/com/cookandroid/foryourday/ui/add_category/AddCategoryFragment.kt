package com.cookandroid.foryourday.ui.add_category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.databinding.FragmentAddCategoryBinding
import com.cookandroid.foryourday.main.MainViewModel
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.ColorData
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class AddCategoryFragment : androidx.fragment.app.Fragment(){
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    private var layoutChosenColor: LinearLayout? = null
    private var layoutCreateCategory: LinearLayout? = null
    private var layoutCategoryBtns: LinearLayout? = null
    private var colorString: String = "#e0e0e0"
    private var mainViewModel: MainViewModel? = null
    private var addCategoryRecyclerViewAdapter: AddCategoryRecyclerViewAdapter? = null
    private var btnDeleteComplete: AppCompatButton? = null
    private var btnDeleteCancel: AppCompatButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutChosenColor = binding.layoutChosenColor
        layoutCreateCategory = binding.layoutCreateCategory
        layoutCategoryBtns = binding.layoutCategoryBtns
        btnDeleteComplete = binding.btnDeleteComplete
        btnDeleteCancel = binding.btnDeleteCancel
        val btnDeleteCategory = binding.btnDeleteCategory
        val btnAddCategory = binding.btnAddCategory
        val edtCategoryName = binding.edtCategoryName
        val addCategoryMessage = binding.addCategoryMessage
        val categoryRecyclerview = binding.categoryRecyclerview
        val btnColorPicker = binding.btnColorPicker
        val btnCancelCreate = binding.btnCancelCreate
        val btnCreateCategory = binding.btnCreateCategory
        val categories = mainViewModel!!.categories.value

        addCategoryRecyclerViewAdapter = AddCategoryRecyclerViewAdapter(categories!! , context!!, false)
        categoryRecyclerview.layoutManager = LinearLayoutManager(context)
        categoryRecyclerview.adapter = addCategoryRecyclerViewAdapter

        if(categories.isEmpty()){
            addCategoryMessage.visibility = View.VISIBLE
            btnDeleteCategory.visibility = View.GONE
        }

        mainViewModel!!.categories.observe(requireActivity(), Observer{
            addCategoryRecyclerViewAdapter!!.setValue(it!!,false)
            if (it.isEmpty()){
                addCategoryMessage.visibility = View.VISIBLE
                btnAddCategory.visibility = View.VISIBLE
                btnDeleteCategory.visibility = View.GONE
            }else{
                btnDeleteCategory.visibility = View.VISIBLE
                btnAddCategory.visibility = View.VISIBLE
                addCategoryMessage.visibility = View.GONE
            }
        })

        btnDeleteCategory.setOnClickListener {
            btnAddCategory.visibility = View.GONE
            btnDeleteCategory.visibility = View.GONE
            btnDeleteComplete!!.visibility = View.VISIBLE
            btnDeleteCancel!!.visibility = View.VISIBLE
            addCategoryRecyclerViewAdapter!!.setValue(null, true)
        }

        btnDeleteCancel!!.setOnClickListener {
            btnAddCategory.visibility = View.VISIBLE
            btnDeleteCategory.visibility = View.VISIBLE
            btnDeleteComplete!!.visibility = View.GONE
            btnDeleteCancel!!.visibility = View.GONE
            addCategoryRecyclerViewAdapter!!.setValue(null, false)
        }

        btnDeleteComplete!!.setOnClickListener {
            deleteApi()
        }

        btnAddCategory.setOnClickListener {
            layoutCreateCategory!!.visibility = View.VISIBLE
            layoutCategoryBtns!!.visibility = View.GONE
        }

        btnCancelCreate.setOnClickListener {
            layoutCategoryBtns!!.visibility = View.VISIBLE
            layoutCreateCategory!!.visibility = View.GONE
        }

        btnColorPicker.setOnClickListener {
            openColorPicker()
        }

        btnCreateCategory.setOnClickListener {
            if(edtCategoryName.text.toString().replace(" ", "") != ""){
                val colorData = ColorData(edtCategoryName.text.toString(), colorString, null)
                postApi(colorData)
                edtCategoryName.text.clear()
            }else{
                Toast.makeText(context, "카테고리 이름을 설정해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openColorPicker(){
        MaterialColorPickerDialog
            .Builder(context!!)
            .setTitle("색상 선택하기")
            .setColorSwatch(ColorSwatch._300)
            .setColorListener { color, colorHex ->
                layoutChosenColor!!.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)
                colorString = colorHex
            }.show()
    }

    private fun deleteApi(){
        val list = addCategoryRecyclerViewAdapter!!.selectedList
        val lastValue = list.get(list.size -1)
        for(id in list){
            ApiInterface.create().deleteCategory(mainViewModel!!.getAuthorization(), id).enqueue(
                object : retrofit2.Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.isSuccessful){
                            Log.d("deleteCategory", "id 카테고리가 삭제되었습니다.")
                            if (id == lastValue){
                                Toast.makeText(context, "삭제가 완료되었습니다!", Toast.LENGTH_SHORT).show()
                                btnDeleteComplete!!.visibility = View.GONE
                                btnDeleteCancel!!.visibility = View.GONE
                                mainViewModel!!.updateCategories()
                            }
                        }else{
                            if(response.code() in 400..500){
                                val jOBjectError = JSONObject(response.errorBody()!!.charStream().readText())
                                Log.d("deleteCategory", jOBjectError.getJSONArray("errors").getJSONObject(0).getString("message"))
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("deleteCategory", "error: ${t.toString()}")
                    }
                }
            )
        }
    }

    private fun postApi(colorData: ColorData){
        ApiInterface.create().addCategory(mainViewModel!!.getAuthorization(), colorData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Toast.makeText(context, "카테고리가 생성되었습니다!☺", Toast.LENGTH_SHORT).show()
                        mainViewModel!!.updateCategories()
                        layoutCreateCategory!!.visibility = View.GONE
                        layoutCategoryBtns!!.visibility = View.VISIBLE
                    }else{
                        if (response.code() in 400..500){
                            val jOBjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jOBjectError.getJSONArray("errors").getJSONObject(0).getString("message"),Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("categoryPostApi", "error: ${t.toString()}")
                }
            }
        )
    }
}