package com.cookandroid.foryourday.ui.add_category

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.databinding.FragmentAddCategoryBinding
import com.cookandroid.foryourday.main.MainViewModel
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.ColorData
import com.cookandroid.foryourday.ui.add_todo.AddToDoRecyclerViewAdapter
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class AddCategoryFragment : Fragment(){
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    private var layoutChosenColor: LinearLayout? = null
    private var layoutCreateCategory: LinearLayout? = null
    private var layoutAddCategory: LinearLayout? = null
    private var colorString: String? = null
    private var mainViewModel: MainViewModel? = null
    private var addCategoryRecyclerViewAdapter: AddCategoryRecyclerViewAdapter? = null
    private var edtCategoryName: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutChosenColor = binding.layoutChosenColor
        layoutCreateCategory = binding.layoutCreateCategory
        layoutAddCategory = binding.layoutAddCategory
        edtCategoryName = binding.edtCategoryName
        val addCategoryMessage = binding.addCategoryMessage
        val categoryRecyclerview = binding.categoryRecyclerview
        val btnColorPicker = binding.btnColorPicker
        val btnCancelCreate = binding.btnCancelCreate
        val btnCreateCategory = binding.btnCreateCategory
        val categories = mainViewModel!!.categories.value

        mainViewModel!!.categories.observe(requireActivity(), Observer{
            if (addCategoryRecyclerViewAdapter != null){
                addCategoryRecyclerViewAdapter!!.setData(it!!)
                addCategoryMessage.visibility = View.INVISIBLE
            }else{
                addCategoryMessage.visibility = View.VISIBLE
            }
        })

        if(categories != null){
            addCategoryRecyclerViewAdapter = AddCategoryRecyclerViewAdapter(categories , context!!)
            categoryRecyclerview.layoutManager = LinearLayoutManager(context)
            categoryRecyclerview.adapter = addCategoryRecyclerViewAdapter
        }

        layoutAddCategory!!.setOnClickListener {
            layoutCreateCategory!!.visibility = View.VISIBLE
            layoutAddCategory!!.visibility = View.GONE
        }

        btnCancelCreate.setOnClickListener {
            layoutAddCategory!!.visibility = View.VISIBLE
            layoutCreateCategory!!.visibility = View.GONE
        }

        btnColorPicker.setOnClickListener {
            openColorPicker()
        }

        btnCreateCategory.setOnClickListener {
            val colorData = ColorData(edtCategoryName!!.text.toString(), colorString!!)
            postApi(colorData)
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

    private fun postApi(colorData: ColorData){
        val accessToken = mainViewModel!!.data.value!!.oauth.accessToken
        val header = "bare $accessToken"

        ApiInterface.create().addCategory(header, colorData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Toast.makeText(context, "카테고리가 생성되었습니다!☺", Toast.LENGTH_SHORT).show()
                        mainViewModel!!.updateCategories()
                        layoutCreateCategory!!.visibility = View.GONE
                        layoutAddCategory!!.visibility = View.VISIBLE
                        edtCategoryName!!.hideKeyBoard()
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

    private fun View.hideKeyBoard(){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}